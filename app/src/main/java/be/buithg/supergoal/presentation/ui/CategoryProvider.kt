package be.buithg.supergoal.presentation.ui

import be.buithg.supergoal.domain.model.GoalCategory

object CategoryProvider {
    val categories = listOf(
        Category(id = 0, title = "Mind", goalCategory = GoalCategory.MIND),
        Category(id = 1, title = "Body", goalCategory = GoalCategory.BODY),
        Category(id = 2, title = "Career", goalCategory = GoalCategory.CAREER),
        Category(id = 3, title = "Money", goalCategory = GoalCategory.MONEY),
        Category(id = 4, title = "Social", goalCategory = GoalCategory.SOCIAL),
        Category(id = 5, title = "Other", goalCategory = GoalCategory.OTHER),
    )
}
