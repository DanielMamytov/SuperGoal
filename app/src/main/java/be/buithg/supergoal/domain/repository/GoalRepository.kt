package be.buithg.supergoal.domain.repository

import be.buithg.supergoal.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun observeGoals(): Flow<List<Goal>>
    fun observeActiveGoals(): Flow<List<Goal>>
    fun observeCompletedGoals(): Flow<List<Goal>>
    fun observeGoalById(goalId: Long): Flow<Goal?>
    suspend fun upsertGoal(goal: Goal)
    suspend fun deleteGoal(goalId: Long)
    suspend fun updateSubGoalStatus(subGoalId: Long, isCompleted: Boolean)
}
