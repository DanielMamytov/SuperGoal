package be.buithg.supergoal.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import be.buithg.supergoal.data.local.converter.GoalCategoryConverter
import be.buithg.supergoal.data.local.dao.GoalDao
import be.buithg.supergoal.data.local.entity.GoalEntity
import be.buithg.supergoal.data.local.entity.SubGoalEntity

@Database(
    entities = [GoalEntity::class, SubGoalEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(GoalCategoryConverter::class)
abstract class SuperGoalDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao

    companion object {
        const val NAME = "super_goal_db"
    }
}
