package be.buithg.supergoal.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_goals",
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["goal_id"],
            childColumns = ["goal_owner_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["goal_owner_id"])]
)
data class SubGoalEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sub_goal_id")
    val id: Long = 0L,
    @ColumnInfo(name = "goal_owner_id")
    val goalId: Long,
    val title: String,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean
)
