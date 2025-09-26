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
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.Job
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
    private var observeGoalsJob: Job? = null

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
        val previousState = _uiState.value
        val updatedSubGoals = previousState.subGoals.map { subGoal ->
            if (subGoal.id == id) subGoal.copy(isChecked = isChecked) else subGoal
        }
        val updatedStatus = if (
            previousState.challengeStatus == ChallengeStatus.Completed &&
            updatedSubGoals.any { !it.isChecked }
        ) {
            ChallengeStatus.Active
        } else {
            previousState.challengeStatus
        }

        _uiState.update { state ->
            state.copy(
                subGoals = updatedSubGoals,
                challengeStatus = updatedStatus,
            )
        }

        if (previousState.goalId != null) {
            viewModelScope.launch {
                goalUseCases.updateSubGoalStatus(id, isChecked)
            }
        }
    }

    fun onStartChallenge() {
        if (_uiState.value.goalId != null) {
            sendMessage(R.string.challenge_detail_goal_already_started)
            return
        }

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

    fun onCompleteChallenge() {
        val state = _uiState.value
        if (state.goalId == null) {
            sendMessage(R.string.challenge_detail_goal_missing)
            return
        }

        if (!state.canCompleteChallenge) {
            sendMessage(R.string.challenge_detail_incomplete_subgoals)
            return
        }

        _uiState.update { current ->
            current.copy(challengeStatus = ChallengeStatus.Completed)
        }
        sendMessage(R.string.challenge_detail_complete_success)
    }

    fun onPerformAgain() {
        val state = _uiState.value
        if (state.goalId == null) {
            sendMessage(R.string.challenge_detail_goal_missing)
            return
        }

        if (state.subGoals.isEmpty()) {
            return
        }

        _uiState.update { current ->
            current.copy(
                challengeStatus = ChallengeStatus.Active,
                subGoals = current.subGoals.map { it.copy(isChecked = false) },
            )
        }

        viewModelScope.launch {
            state.subGoals.forEach { subGoal ->
                goalUseCases.updateSubGoalStatus(subGoal.id, false)
            }
            sendMessage(R.string.challenge_detail_reset_message)
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
        val (formattedDeadline, durationDays) = formatDeadline(deadlineMillis, challenge.durationDays)

        _uiState.value = ChallengeDetailUiState(
            isLoading = false,
            title = challenge.title,
            category = challenge.category,
            subGoals = challenge.subgoals.mapIndexed { index, title ->
                ChallengeSubGoalUi(id = index.toLong(), title = title)
            },
            deadlineText = formattedDeadline,
            durationDays = durationDays,
            illustrationRes = challenge.imageRes,
        )

        observeChallengeProgress(challenge)
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

    private fun observeChallengeProgress(challenge: Challenge) {
        observeGoalsJob?.cancel()
        observeGoalsJob = viewModelScope.launch {
            goalUseCases.observeGoals().collect { goals ->
                val matchingGoal = goals.firstOrNull { it.title == challenge.title }

                _uiState.update { state ->
                    if (matchingGoal == null) {
                        state.copy(
                            goalId = null,
                            challengeStatus = ChallengeStatus.NotStarted,
                        )
                    } else {
                        val (deadlineText, durationDays) = formatDeadline(
                            matchingGoal.deadlineMillis,
                            state.durationDays.takeIf { it > 0 } ?: challenge.durationDays,
                        )
                        state.copy(
                            goalId = matchingGoal.id,
                            challengeStatus = if (matchingGoal.isCompleted) {
                                ChallengeStatus.Completed
                            } else {
                                ChallengeStatus.Active
                            },
                            subGoals = matchingGoal.subGoals.map { subGoal ->
                                ChallengeSubGoalUi(
                                    id = subGoal.id,
                                    title = subGoal.title,
                                    isChecked = subGoal.isCompleted,
                                )
                            },
                            deadlineText = deadlineText,
                            durationDays = durationDays,
                        )
                    }
                }
            }
        }
    }

    private fun formatDeadline(deadlineMillis: Long, fallbackDuration: Int): Pair<String, Int> {
        val formattedDeadline = synchronized(dateFormatter) {
            dateFormatter.format(Calendar.getInstance().apply {
                timeInMillis = deadlineMillis
            }.time)
        }

        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffMillis = deadlineMillis - startOfToday.timeInMillis
        val dayInMillis = TimeUnit.DAYS.toMillis(1)
        val remainingDays = if (diffMillis <= 0L) {
            0
        } else {
            ((diffMillis + dayInMillis - 1) / dayInMillis).toInt()
        }

        val durationDays = if (remainingDays > 0) remainingDays else fallbackDuration
        return formattedDeadline to durationDays
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
