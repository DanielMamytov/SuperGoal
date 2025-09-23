package be.buithg.supergoal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import be.buithg.supergoal.data.local.entity.GoalEntity
import be.buithg.supergoal.data.local.entity.SubGoalEntity
import be.buithg.supergoal.data.local.relation.GoalWithSubGoals
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Transaction
    @Query("SELECT * FROM goals ORDER BY archived_at_millis IS NULL DESC, deadline_millis ASC")
    fun observeGoals(): Flow<List<GoalWithSubGoals>>

    @Transaction
    @Query("SELECT * FROM goals WHERE goal_id = :goalId")
    fun observeGoal(goalId: Long): Flow<GoalWithSubGoals?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubGoals(subGoals: List<SubGoalEntity>)

    @Query("DELETE FROM sub_goals WHERE goal_owner_id = :goalId")
    suspend fun deleteSubGoalsForGoal(goalId: Long)

    @Query("UPDATE sub_goals SET is_completed = :isCompleted WHERE sub_goal_id = :subGoalId")
    suspend fun updateSubGoalCompletion(subGoalId: Long, isCompleted: Boolean)

    @Query("DELETE FROM goals WHERE goal_id = :goalId")
    suspend fun deleteGoal(goalId: Long)
}
