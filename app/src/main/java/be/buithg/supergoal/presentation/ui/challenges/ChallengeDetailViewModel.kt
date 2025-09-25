package be.buithg.supergoal.presentation.ui.challenges

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.buithg.supergoal.R
import be.buithg.supergoal.domain.model.Goal
import be.buithg.supergoal.domain.model.GoalCategory
import be.buithg.supergoal.domain.model.SubGoal
import be.buithg.supergoal.domain.usecase.GoalUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChallengeDetailViewModel @Inject constructor(
    private val goalUseCases: GoalUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private val _uiState = MutableStateFlow(ChallengeDetailUiState())
    val uiState: StateFlow<ChallengeDetailUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<ChallengeDetailEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private var selectedChallenge: Challenge? = null
    private var computedDeadlineMillis: Long? = null

    init {
        val challengeId = savedStateHandle.get<Int>("challengeId") ?: -1
        if (challengeId == -1) {
            sendMessage(R.string.challenge_detail_missing)
            sendEvent(ChallengeDetailEvent.CloseScreen)
        } else {
            loadChallenge(challengeId)
        }
    }

    fun onSubGoalChecked(id: Long, isChecked: Boolean) {
        _uiState.update { state ->
            state.copy(
                subGoals = state.subGoals.map { subGoal ->
                    if (subGoal.id == id) subGoal.copy(isChecked = isChecked) else subGoal
                },
            )
        }
    }

    fun onStartChallenge() {
        val challenge = selectedChallenge
        val deadlineMillis = computedDeadlineMillis
        if (challenge == null || deadlineMillis == null) {
            sendMessage(R.string.challenge_detail_missing)
            sendEvent(ChallengeDetailEvent.CloseScreen)
            return
        }

        val goal = Goal(
            title = challenge.title,
            category = GoalCategory.fromRaw(challenge.category),
            deadlineMillis = deadlineMillis,
            imageUri = null,
            createdAtMillis = System.currentTimeMillis(),
            subGoals = challenge.subgoals.map { title -> SubGoal(title = title) },
        )

        viewModelScope.launch {
            goalUseCases.upsertGoal(goal)
            sendMessage(R.string.challenge_detail_toast)
            sendEvent(ChallengeDetailEvent.CloseScreen)
        }
    }

    private fun loadChallenge(challengeId: Int) {
        val challenge = ChallengeDataSource.getChallenges().firstOrNull { it.id == challengeId }
        if (challenge == null) {
            sendMessage(R.string.challenge_detail_missing)
            sendEvent(ChallengeDetailEvent.CloseScreen)
            return
        }

        selectedChallenge = challenge
        val deadlineMillis = calculateDeadlineMillis(challenge.durationDays)
        computedDeadlineMillis = deadlineMillis
        val formattedDeadline = synchronized(dateFormatter) {
            dateFormatter.format(Calendar.getInstance().apply {
                timeInMillis = deadlineMillis
            }.time)
        }

        _uiState.value = ChallengeDetailUiState(
            isLoading = false,
            title = challenge.title,
            category = challenge.category,
            subGoals = challenge.subgoals.mapIndexed { index, title ->
                ChallengeSubGoalUi(id = index.toLong(), title = title)
            },
            deadlineText = formattedDeadline,
            durationDays = challenge.durationDays,
            illustrationRes = challenge.imageRes,
        )
    }

    private fun calculateDeadlineMillis(durationDays: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
            add(Calendar.DAY_OF_YEAR, durationDays)
        }
        return calendar.timeInMillis
    }

    private fun sendMessage(@StringRes messageRes: Int) {
        eventsChannel.trySend(ChallengeDetailEvent.ShowMessage(messageRes))
    }

    private fun sendEvent(event: ChallengeDetailEvent) {
        eventsChannel.trySend(event)
    }
}

sealed interface ChallengeDetailEvent {
    data class ShowMessage(@StringRes val messageRes: Int) : ChallengeDetailEvent
    object CloseScreen : ChallengeDetailEvent
}
