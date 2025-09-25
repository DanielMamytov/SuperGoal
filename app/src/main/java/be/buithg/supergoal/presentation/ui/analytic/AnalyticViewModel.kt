package be.buithg.supergoal.presentation.ui.analytic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.buithg.supergoal.domain.model.Goal
import be.buithg.supergoal.domain.model.GoalCategory
import be.buithg.supergoal.domain.usecase.GoalUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AnalyticViewModel @Inject constructor(
    goalUseCases: GoalUseCases,
) : ViewModel() {

    val uiState: StateFlow<AnalyticUiState> = goalUseCases.observeGoals()
        .map(::toUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AnalyticUiState(),
        )

    private fun toUiState(goals: List<Goal>): AnalyticUiState {
        val totalSubGoals = goals.sumOf { it.subGoals.size }
        val completedSubGoals = goals.sumOf { goal -> goal.subGoals.count { subGoal -> subGoal.isCompleted } }

        val hasOverallProgress = totalSubGoals > 0
        val overallProgressPercentage = if (hasOverallProgress) {
            ((completedSubGoals.toDouble() / totalSubGoals.toDouble()) * 100).roundToInt()
        } else {
            0
        }

        val shares = goals
            .filter(Goal::isCompleted)
            .mapNotNull { goal ->
                val endTime = goal.archivedAtMillis ?: goal.deadlineMillis
                val duration = (endTime - goal.createdAtMillis).coerceAtLeast(0L)
                if (duration == 0L) {
                    null
                } else {
                    goal.category to duration
                }
            }
            .groupBy(
                keySelector = { (category, _) -> category },
                valueTransform = { (_, duration) -> duration },
            )
            .mapNotNull { (category, durations) ->
                val totalDuration = durations.sum()
                if (totalDuration <= 0L) {
                    null
                } else {
                    category to totalDuration
                }
            }
            .let { groupedDurations ->
                val grandTotal = groupedDurations.sumOf { (_, duration) -> duration }
                if (grandTotal <= 0L) {
                    emptyList()
                } else {
                    groupedDurations
                        .map { (category, duration) ->
                            val percent = (duration.toDouble() / grandTotal.toDouble()) * 100
                            CategoryShare(
                                category = category,
                                percentage = percent,
                            )
                        }
                        .filter { it.percentage > 0.0 }
                        .sortedByDescending(CategoryShare::percentage)
                }
            }

        return AnalyticUiState(
            hasOverallProgress = hasOverallProgress,
            overallProgress = overallProgressPercentage,
            categoryShares = shares,
        )
    }
}

data class AnalyticUiState(
    val hasOverallProgress: Boolean = false,
    val overallProgress: Int = 0,
    val categoryShares: List<CategoryShare> = emptyList(),
) {
    val hasCategoryShares: Boolean get() = categoryShares.isNotEmpty()
    val shouldShowEmptyState: Boolean get() = !hasOverallProgress && categoryShares.isEmpty()
}

data class CategoryShare(
    val category: GoalCategory,
    val percentage: Double,
)
