package be.buithg.supergoal.domain.usecase

import be.buithg.supergoal.domain.model.Goal
import be.buithg.supergoal.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGoalsUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke(): Flow<List<Goal>> = repository.observeGoals()
}
