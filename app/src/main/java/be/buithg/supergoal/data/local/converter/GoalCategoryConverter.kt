package be.buithg.supergoal.data.local.converter

import androidx.room.TypeConverter
import be.buithg.supergoal.domain.model.GoalCategory

class GoalCategoryConverter {
    @TypeConverter
    fun toGoalCategory(value: String?): GoalCategory? = value?.let(GoalCategory::fromRaw)

    @TypeConverter
    fun fromGoalCategory(category: GoalCategory?): String? = category?.name
}
