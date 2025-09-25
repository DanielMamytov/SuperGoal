package be.buithg.supergoal.presentation.ui.challenges

import androidx.annotation.DrawableRes

data class ChallengeDetailUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val category: String = "",
    val subGoals: List<ChallengeSubGoalUi> = emptyList(),
    val deadlineText: String = "",
    val durationDays: Int = 0,
    @DrawableRes val illustrationRes: Int = 0,
) {
    val hasContent: Boolean get() = title.isNotBlank()
    val isSubGoalListEmpty: Boolean get() = subGoals.isEmpty()
}

data class ChallengeSubGoalUi(
    val id: Long,
    val title: String,
    val isChecked: Boolean = false,
)
