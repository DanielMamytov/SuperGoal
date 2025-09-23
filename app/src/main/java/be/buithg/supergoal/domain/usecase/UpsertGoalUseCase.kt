package be.buithg.supergoal.domain.usecase

import be.buithg.supergoal.domain.model.Goal
import be.buithg.supergoal.domain.repository.GoalRepository
import javax.inject.Inject

class UpsertGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal) = repository.upsertGoal(goal)
}
