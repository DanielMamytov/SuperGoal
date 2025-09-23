package be.buithg.supergoal.data.repository

import be.buithg.supergoal.data.local.dao.GoalDao
import be.buithg.supergoal.data.mapper.GoalMapper
import be.buithg.supergoal.di.IoDispatcher
import be.buithg.supergoal.domain.model.Goal
import be.buithg.supergoal.domain.repository.GoalRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val mapper: GoalMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GoalRepository {

    override fun observeGoals(): Flow<List<Goal>> =
        goalDao.observeGoals().map { relations ->
            relations.map(mapper::toDomain)
                .sortedWith(compareByDescending<Goal> { it.progress }
                    .thenBy { it.deadlineMillis })
        }

    override fun observeActiveGoals(): Flow<List<Goal>> =
        observeGoals().map { goals -> goals.filterNot(Goal::isCompleted) }

    override fun observeCompletedGoals(): Flow<List<Goal>> =
        observeGoals().map { goals -> goals.filter(Goal::isCompleted) }

    override fun observeGoalById(goalId: Long): Flow<Goal?> =
        goalDao.observeGoal(goalId).map { relation -> relation?.let(mapper::toDomain) }

    override suspend fun upsertGoal(goal: Goal) = withContext(ioDispatcher) {
        val insertedId = goalDao.insertGoal(mapper.toEntity(goal))
        val goalId = if (goal.id == 0L) insertedId else goal.id
        goalDao.deleteSubGoalsForGoal(goalId)
        val subGoalEntities = mapper.toSubGoalEntities(goalId, goal.subGoals)
        if (subGoalEntities.isNotEmpty()) {
            goalDao.insertSubGoals(subGoalEntities)
        }
    }

    override suspend fun deleteGoal(goalId: Long) = withContext(ioDispatcher) {
        goalDao.deleteGoal(goalId)
    }

    override suspend fun updateSubGoalStatus(subGoalId: Long, isCompleted: Boolean) =
        withContext(ioDispatcher) {
            goalDao.updateSubGoalCompletion(subGoalId, isCompleted)
        }
}
