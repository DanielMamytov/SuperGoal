package be.buithg.supergoal.di

import android.content.Context
import androidx.room.Room
import be.buithg.supergoal.data.local.dao.GoalDao
import be.buithg.supergoal.data.local.database.SuperGoalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SuperGoalDatabase = Room.databaseBuilder(
        context,
        SuperGoalDatabase::class.java,
        SuperGoalDatabase.NAME
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideGoalDao(database: SuperGoalDatabase): GoalDao = database.goalDao()
}
