package be.buithg.supergoal.domain.model

/**
 * A sub-task within a goal. Completion of all sub-goals indicates that the parent goal is finished.
 */
data class SubGoal(
    val id: Long = 0L,
    val goalId: Long = 0L,
    val title: String,
    val isCompleted: Boolean = false
)
