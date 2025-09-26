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
    val goalId: Long? = null,
    val challengeStatus: ChallengeStatus = ChallengeStatus.NotStarted,
) {
    val hasContent: Boolean get() = title.isNotBlank()
    val isSubGoalListEmpty: Boolean get() = subGoals.isEmpty()
    val isChallengeStarted: Boolean get() = challengeStatus != ChallengeStatus.NotStarted
    val isChallengeCompleted: Boolean get() = challengeStatus == ChallengeStatus.Completed
    val canCompleteChallenge: Boolean
        get() = subGoals.isNotEmpty() && subGoals.all(ChallengeSubGoalUi::isChecked)
}

data class ChallengeSubGoalUi(
    val id: Long,
    val title: String,
    val isChecked: Boolean = false,
)

enum class ChallengeStatus {
    NotStarted,
    Active,
    Completed,
}
