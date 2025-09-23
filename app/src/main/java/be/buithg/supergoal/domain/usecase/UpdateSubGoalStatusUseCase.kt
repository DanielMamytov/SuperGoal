package be.buithg.supergoal.domain.usecase

import be.buithg.supergoal.domain.repository.GoalRepository
import javax.inject.Inject

class UpdateSubGoalStatusUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(subGoalId: Long, isCompleted: Boolean) =
        repository.updateSubGoalStatus(subGoalId, isCompleted)
}
