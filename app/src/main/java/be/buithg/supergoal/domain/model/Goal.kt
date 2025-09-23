package be.buithg.supergoal.domain.model

/**
 * Represents a long term objective created by the user. Goals are composed of sub-goals that drive
 * the completion progress tracked across the application.
 */
data class Goal(
    val id: Long = 0L,
    val title: String,
    val category: GoalCategory,
    val deadlineMillis: Long,
    val imageUri: String?,
    val createdAtMillis: Long,
    val archivedAtMillis: Long? = null,
    val progress: Double = 0.0,
    val subGoals: List<SubGoal> = emptyList()
) {
    val isCompleted: Boolean get() = progress >= 100.0
}
