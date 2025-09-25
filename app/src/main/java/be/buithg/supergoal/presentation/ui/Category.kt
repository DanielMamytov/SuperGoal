package be.buithg.supergoal.presentation.ui

import be.buithg.supergoal.domain.model.GoalCategory

data class Category(
    val id: Int,
    val title: String,
    val goalCategory: GoalCategory,
)
