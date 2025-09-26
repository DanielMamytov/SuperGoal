package be.buithg.supergoal.domain.usecase

import javax.inject.Inject

data class GoalUseCases @Inject constructor(
    val observeGoals: ObserveGoalsUseCase,
    val observeActiveGoals: ObserveActiveGoalsUseCase,
    val observeCompletedGoals: ObserveCompletedGoalsUseCase,
    val observeGoalById: ObserveGoalByIdUseCase,
    val upsertGoal: UpsertGoalUseCase,
    val deleteGoal: DeleteGoalUseCase,
    val updateSubGoalStatus: UpdateSubGoalStatusUseCase,
    val reactivateGoal: ReactivateGoalUseCase,
)
