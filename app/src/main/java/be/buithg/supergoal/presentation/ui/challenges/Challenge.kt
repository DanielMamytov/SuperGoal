package be.buithg.supergoal.presentation.ui.challenges

import androidx.annotation.DrawableRes

/**
 * Represents a pre-defined challenge that users can adopt as a goal.
 */
data class Challenge(
    val id: Int,
    val title: String,
    val category: String,
    val durationDays: Int,
    val subgoals: List<String>,
    @DrawableRes val imageRes: Int,
)
