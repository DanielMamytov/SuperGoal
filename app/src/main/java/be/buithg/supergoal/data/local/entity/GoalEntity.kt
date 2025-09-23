package be.buithg.supergoal.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import be.buithg.supergoal.domain.model.GoalCategory

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "goal_id")
    val id: Long = 0L,
    val title: String,
    val category: GoalCategory,
    @ColumnInfo(name = "deadline_millis")
    val deadlineMillis: Long,
    @ColumnInfo(name = "image_uri")
    val imageUri: String?,
    @ColumnInfo(name = "created_at_millis")
    val createdAtMillis: Long,
    @ColumnInfo(name = "archived_at_millis")
    val archivedAtMillis: Long? = null
)
