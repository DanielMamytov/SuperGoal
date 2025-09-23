package be.buithg.supergoal.domain.usecase

import be.buithg.supergoal.domain.repository.GoalRepository
import javax.inject.Inject

class DeleteGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goalId: Long) = repository.deleteGoal(goalId)
}
