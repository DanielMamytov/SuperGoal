package be.buithg.supergoal.di

import be.buithg.supergoal.domain.usecase.DeleteGoalUseCase
import be.buithg.supergoal.domain.usecase.GoalUseCases
import be.buithg.supergoal.domain.usecase.ObserveActiveGoalsUseCase
import be.buithg.supergoal.domain.usecase.ObserveCompletedGoalsUseCase
import be.buithg.supergoal.domain.usecase.ObserveGoalByIdUseCase
import be.buithg.supergoal.domain.usecase.ObserveGoalsUseCase
import be.buithg.supergoal.domain.usecase.ReactivateGoalUseCase
import be.buithg.supergoal.domain.usecase.UpdateSubGoalStatusUseCase
import be.buithg.supergoal.domain.usecase.UpsertGoalUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGoalUseCases(
        observeGoals: ObserveGoalsUseCase,
        observeActiveGoals: ObserveActiveGoalsUseCase,
        observeCompletedGoals: ObserveCompletedGoalsUseCase,
        observeGoalById: ObserveGoalByIdUseCase,
        upsertGoal: UpsertGoalUseCase,
        deleteGoal: DeleteGoalUseCase,
        updateSubGoalStatus: UpdateSubGoalStatusUseCase,
        reactivateGoal: ReactivateGoalUseCase,
    ): GoalUseCases = GoalUseCases(
        observeGoals = observeGoals,
        observeActiveGoals = observeActiveGoals,
        observeCompletedGoals = observeCompletedGoals,
        observeGoalById = observeGoalById,
        upsertGoal = upsertGoal,
        deleteGoal = deleteGoal,
        updateSubGoalStatus = updateSubGoalStatus,
        reactivateGoal = reactivateGoal,
    )
}
