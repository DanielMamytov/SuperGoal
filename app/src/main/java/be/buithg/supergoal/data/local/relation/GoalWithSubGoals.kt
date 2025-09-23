package be.buithg.supergoal.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import be.buithg.supergoal.data.local.entity.GoalEntity
import be.buithg.supergoal.data.local.entity.SubGoalEntity

data class GoalWithSubGoals(
    @Embedded val goal: GoalEntity,
    @Relation(
        parentColumn = "goal_id",
        entityColumn = "goal_owner_id"
    )
    val subGoals: List<SubGoalEntity>
)
