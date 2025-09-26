package be.buithg.supergoal.presentation.ui.goal

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
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class GoalDetailViewModel @Inject constructor(
    private val goalUseCases: GoalUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private val _uiState = MutableStateFlow(GoalDetailUiState())
    val uiState: StateFlow<GoalDetailUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<GoalDetailEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private val goalId: Long = savedStateHandle.get<Long>("goalId") ?: 0L
    private var currentGoal: Goal? = null

    init {
        if (goalId == 0L) {
            eventsChannel.trySend(GoalDetailEvent.CloseScreen)
        } else {
            observeGoal(goalId)
        }
    }

    fun onDeleteGoal() {
        val id = _uiState.value.goalId ?: return
        viewModelScope.launch {
            goalUseCases.deleteGoal(id)
            eventsChannel.send(GoalDetailEvent.GoalDeleted(R.string.toast_goal_deleted))
        }
    }

    fun onSubGoalToggled(subGoalId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            goalUseCases.updateSubGoalStatus(subGoalId, isCompleted)

            if (!isCompleted) {
                val goal = currentGoal
                if (goal?.archivedAtMillis != null) {
                    goalUseCases.reactivateGoal(goal.id)
                }
            }
        }
    }

    private fun observeGoal(goalId: Long) {
        goalUseCases.observeGoalById(goalId)
            .onEach { goal ->
                if (goal == null) {
                    eventsChannel.trySend(GoalDetailEvent.CloseScreen)
                } else {
                    currentGoal = goal
                    _uiState.value = goal.toUiState()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun Goal.toUiState(): GoalDetailUiState {
        val safeProgress = progress.coerceIn(0.0, 100.0)
        val progressValue = (safeProgress * 10).roundToInt()
        val formattedProgress = String.format(Locale.getDefault(), "%.1f%%", safeProgress)
        val formattedDeadline = synchronized(dateFormatter) {
            dateFormatter.format(Date(deadlineMillis))
        }
        val subGoalItems = subGoals.map { it.toUiItem() }
        val displayCategory = category.toDisplayName()
        return GoalDetailUiState(
            goalId = id,
            title = title,
            category = displayCategory,
            progressText = formattedProgress,
            progressValue = progressValue,
            deadline = formattedDeadline,
            imageUri = imageUri,
            subGoals = subGoalItems,
        )
    }

    private fun SubGoal.toUiItem(): GoalDetailSubGoalItem = GoalDetailSubGoalItem(
        id = id,
        title = title,
        isCompleted = isCompleted,
    )

    private fun GoalCategory.toDisplayName(): String {
        val lowercase = name.lowercase(Locale.getDefault())
        return lowercase.replaceFirstChar { character ->
            if (character.isLowerCase()) {
                character.titlecase(Locale.getDefault())
            } else {
                character.toString()
            }
        }
    }
}

data class GoalDetailUiState(
    val goalId: Long? = null,
    val title: String = "",
    val category: String = "",
    val progressText: String = "0.0%",
    val progressValue: Int = 0,
    val deadline: String = "",
    val imageUri: String? = null,
    val subGoals: List<GoalDetailSubGoalItem> = emptyList(),
) {
    val showPlaceholderImage: Boolean get() = imageUri.isNullOrBlank()
    val isSubGoalListEmpty: Boolean get() = subGoals.isEmpty()
}

data class GoalDetailSubGoalItem(
    val id: Long,
    val title: String,
    val isCompleted: Boolean,
)

sealed class GoalDetailEvent {
    data class GoalDeleted(@StringRes val messageRes: Int) : GoalDetailEvent()
    object CloseScreen : GoalDetailEvent()
}
