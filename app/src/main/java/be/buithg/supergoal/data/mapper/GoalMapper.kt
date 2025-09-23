package be.buithg.supergoal.data.mapper

import be.buithg.supergoal.data.local.entity.GoalEntity
import be.buithg.supergoal.data.local.entity.SubGoalEntity
import be.buithg.supergoal.data.local.relation.GoalWithSubGoals
import be.buithg.supergoal.domain.model.Goal
import be.buithg.supergoal.domain.model.SubGoal
import javax.inject.Inject
import kotlin.math.round

class GoalMapper @Inject constructor() {

    fun toDomain(goalWithSubGoals: GoalWithSubGoals): Goal {
        val goalEntity = goalWithSubGoals.goal
        val subGoals = goalWithSubGoals.subGoals.map(::toDomain)
        val completedSubGoals = subGoals.count(SubGoal::isCompleted)
        val progress = if (subGoals.isEmpty()) {
            0.0
        } else {
            val raw = (completedSubGoals.toDouble() / subGoals.size.toDouble()) * 100
            (round(raw * 10) / 10.0).coerceIn(0.0, 100.0)
        }

        return Goal(
            id = goalEntity.id,
            title = goalEntity.title,
            category = goalEntity.category,
            deadlineMillis = goalEntity.deadlineMillis,
            imageUri = goalEntity.imageUri,
            createdAtMillis = goalEntity.createdAtMillis,
            archivedAtMillis = goalEntity.archivedAtMillis,
            progress = progress,
            subGoals = subGoals
        )
    }

    fun toEntity(goal: Goal): GoalEntity = GoalEntity(
        id = goal.id,
        title = goal.title,
        category = goal.category,
        deadlineMillis = goal.deadlineMillis,
        imageUri = goal.imageUri,
        createdAtMillis = goal.createdAtMillis,
        archivedAtMillis = goal.archivedAtMillis
    )

    fun toSubGoalEntities(goalId: Long, subGoals: List<SubGoal>): List<SubGoalEntity> =
        subGoals.map { subGoal ->
            SubGoalEntity(
                id = subGoal.id,
                goalId = goalId,
                title = subGoal.title,
                isCompleted = subGoal.isCompleted
            )
        }

    private fun toDomain(entity: SubGoalEntity): SubGoal = SubGoal(
        id = entity.id,
        goalId = entity.goalId,
        title = entity.title,
        isCompleted = entity.isCompleted
    )
}
