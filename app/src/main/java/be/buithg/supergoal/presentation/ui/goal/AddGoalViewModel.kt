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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@HiltViewModel
class AddGoalViewModel @Inject constructor(
    private val goalUseCases: GoalUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private val _uiState = MutableStateFlow(AddGoalUiState())
    val uiState: StateFlow<AddGoalUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<AddGoalEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private val editingGoalId: Long? = savedStateHandle.get<Long>("goalId")?.takeIf { it != 0L }

    private var nextTempSubGoalId = -1L

    init {
        editingGoalId?.let(::loadGoal)
    }

    fun onGoalNameChanged(name: String) {
        _uiState.update { state -> state.copy(goalName = name) }
    }

    fun onCategorySelected(category: GoalCategory, title: String) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                selectedCategoryTitle = title,
            )
        }
    }

    fun onDeadlineSelected(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val millis = calendar.timeInMillis
        val formattedDate = synchronized(dateFormatter) {
            dateFormatter.format(calendar.time)
        }
        _uiState.update { state ->
            state.copy(
                deadlineMillis = millis,
                deadlineText = formattedDate,
            )
        }
    }

    fun onImageSelected(imageUri: String?) {
        if (!imageUri.isNullOrBlank()) {
            _uiState.update { state -> state.copy(imageUri = imageUri) }
        }
    }

    fun onAddSubGoal(title: String) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        val newSubGoal = SubGoalItemUi(
            uiId = nextTempSubGoalId,
            subGoalId = null,
            title = trimmed,
        )
        nextTempSubGoalId -= 1

        _uiState.update { state ->
            state.copy(subGoals = state.subGoals + newSubGoal)
        }
    }

    fun onRemoveSubGoal(id: Long) {
        _uiState.update { state ->
            state.copy(subGoals = state.subGoals.filterNot { it.uiId == id })

        }
    }

    fun onSaveGoal() {
        val state = _uiState.value
        if (state.subGoals.isEmpty()) {
            sendMessage(R.string.toast_add_subgoal_first)
            return
        }
        val category = state.selectedCategory
        val deadline = state.deadlineMillis
        val title = state.goalName.trim()
        if (category == null || deadline == null || title.isEmpty()) {
            sendMessage(R.string.toast_fill_all_fields)
            return
        }

        val goal = Goal(
            id = state.goalId ?: 0L,
            title = title,
            category = category,
            deadlineMillis = deadline,
            imageUri = state.imageUri,
            createdAtMillis = state.createdAtMillis ?: System.currentTimeMillis(),
            archivedAtMillis = state.archivedAtMillis,
            subGoals = state.subGoals.map { subGoal ->
                SubGoal(
                    id = subGoal.subGoalId ?: 0L,
                    goalId = state.goalId ?: 0L,
                    title = subGoal.title,
                    isCompleted = subGoal.isCompleted,
                )
            },
        )

        viewModelScope.launch {
            goalUseCases.upsertGoal(goal)
            sendMessage(R.string.toast_goal_saved)
            eventsChannel.send(AddGoalEvent.GoalSaved)
        }
    }

    private fun sendMessage(@StringRes messageRes: Int) {
        eventsChannel.trySend(AddGoalEvent.ShowMessage(messageRes))
    }

    private fun loadGoal(goalId: Long) {
        viewModelScope.launch {
            val goal = goalUseCases.observeGoalById(goalId)
                .firstOrNull()
                ?: return@launch
            val formattedDate = synchronized(dateFormatter) {
                dateFormatter.format(Calendar.getInstance().apply {
                    timeInMillis = goal.deadlineMillis
                }.time)
            }
            val subGoals = goal.subGoals.map { subGoal ->
                SubGoalItemUi(
                    uiId = subGoal.id,
                    subGoalId = subGoal.id,
                    title = subGoal.title,
                    isCompleted = subGoal.isCompleted,
                )
            }
            nextTempSubGoalId = (subGoals.minOfOrNull { it.uiId } ?: 0L).coerceAtMost(0L) - 1
            _uiState.update {
                it.copy(
                    goalId = goal.id,
                    goalName = goal.title,
                    selectedCategory = goal.category,
                    selectedCategoryTitle = null,
                    deadlineMillis = goal.deadlineMillis,
                    deadlineText = formattedDate,
                    subGoals = subGoals,
                    imageUri = goal.imageUri,
                    createdAtMillis = goal.createdAtMillis,
                    archivedAtMillis = goal.archivedAtMillis,
                )
            }
        }
    }
}

data class AddGoalUiState(
    val goalId: Long? = null,
    val goalName: String = "",
    val selectedCategory: GoalCategory? = null,
    val selectedCategoryTitle: String? = null,
    val deadlineMillis: Long? = null,
    val deadlineText: String = "",
    val subGoals: List<SubGoalItemUi> = emptyList(),
    val imageUri: String? = null,
    val createdAtMillis: Long? = null,
    val archivedAtMillis: Long? = null,
)

data class SubGoalItemUi(
    val uiId: Long,
    val subGoalId: Long?,
    val title: String,
    val isCompleted: Boolean = false,
)

sealed class AddGoalEvent {
    data class ShowMessage(@StringRes val messageRes: Int) : AddGoalEvent()
    object GoalSaved : AddGoalEvent()
}
