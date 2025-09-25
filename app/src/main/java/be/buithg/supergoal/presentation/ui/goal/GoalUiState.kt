package be.buithg.supergoal.presentation.ui.goal

import be.buithg.supergoal.domain.model.Goal

data class GoalUiState(
    val goals: List<Goal> = emptyList(),
    val selectedFilter: GoalFilter = GoalFilter.ALL
) {
    val isEmpty: Boolean get() = goals.isEmpty()
}
