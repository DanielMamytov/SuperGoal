package be.buithg.supergoal.presentation.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.buithg.supergoal.domain.model.Goal
import be.buithg.supergoal.domain.model.GoalCategory
import be.buithg.supergoal.domain.usecase.GoalUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalUseCases: GoalUseCases,
) : ViewModel() {

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private val filter = MutableStateFlow(GoalFilter.All)

    val uiState: StateFlow<GoalUiState> = filter
        .flatMapLatest { selectedFilter ->
            val source = when (selectedFilter) {
                GoalFilter.All -> goalUseCases.observeGoals()
                GoalFilter.Active -> goalUseCases.observeActiveGoals()
                GoalFilter.Archived -> goalUseCases.observeCompletedGoals()
            }
            source.map { goals ->
                GoalUiState(
                    selectedFilter = selectedFilter,
                    goals = goals.map(::toGoalListItem),
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GoalUiState(),
        )

    fun onFilterSelected(goalFilter: GoalFilter) {
        if (filter.value != goalFilter) {
            filter.value = goalFilter
        }
    }

    private fun toGoalListItem(goal: Goal): GoalListItem {
        val safeProgress = goal.progress.coerceIn(0.0, 100.0)
        val progressPercentage = safeProgress.roundToInt()
        val formattedDeadline = synchronized(dateFormatter) {
            dateFormatter.format(Date(goal.deadlineMillis))
        }

        return GoalListItem(
            id = goal.id,
            title = goal.title,
            category = goal.category.toDisplayName(),
            progressPercentage = progressPercentage,
            progressText = "$progressPercentage%",
            deadline = formattedDeadline,
            imageUri = goal.imageUri,
        )
    }

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

enum class GoalFilter {
    All,
    Active,
    Archived,
}

data class GoalUiState(
    val selectedFilter: GoalFilter = GoalFilter.All,
    val goals: List<GoalListItem> = emptyList(),
) {
    val isEmpty: Boolean get() = goals.isEmpty()
}

data class GoalListItem(
    val id: Long,
    val title: String,
    val category: String,
    val progressPercentage: Int,
    val progressText: String,
    val deadline: String,
    val imageUri: String?,
)
