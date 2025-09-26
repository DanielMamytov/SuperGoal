package be.buithg.supergoal.presentation.ui.challenges

import be.buithg.supergoal.R

object ChallengeDataSource {

    fun getChallenges(): List<Challenge> = listOf(
        Challenge(
            id = 1,
            title = "Sunrise Scan",
            category = "Body",
            durationDays = 7,
            subgoals = dailyTasks("Drink 2 litres water", 7),
            imageRes = R.drawable.sunrise_scan,
        ),
        Challenge(
            id = 2,
            title = "Inbox Zero-ish",
            category = "Social",
            durationDays = 7,
            subgoals = dailyTasks("Clear five emails", 7),
            imageRes = R.drawable.inbox,
        ),
        Challenge(
            id = 3,
            title = "Micro-Workout",
            category = "Body",
            durationDays = 14,
            subgoals = dailyTasks("5 minutes of movement", 14),
            imageRes = R.drawable.micro_workout,
        ),
        Challenge(
            id = 4,
            title = "Budget Breath",
            category = "Money",
            durationDays = 14,
            subgoals = dailyTasks("Track one expense line per day", 14),
            imageRes = R.drawable.budget_breath,
        ),
        Challenge(
            id = 5,
            title = "Deep Work Dot",
            category = "Career",
            durationDays = 14,
            subgoals = dailyTasks("10 focus minutes with a timer", 14),
            imageRes = R.drawable.deep_work_out,
        ),
        Challenge(
            id = 6,
            title = "Digital Sunset",
            category = "Mind",
            durationDays = 30,
            subgoals = dailyTasks("Screens off 1 hour before bed", 30),
            imageRes = R.drawable.digital_work,
        ),
        Challenge(
            id = 7,
            title = "Gratitude Ping",
            category = "Mind",
            durationDays = 30,
            subgoals = dailyTasks("Write one thankful line", 30),
            imageRes = R.drawable.gratitude,
        ),
        Challenge(
            id = 8,
            title = "Craft Corner",
            category = "Other",
            durationDays = 30,
            subgoals = dailyTasks("Write one thankful line", 30),
            imageRes = R.drawable.craft,
        ),
        Challenge(
            id = 9,
            title = "Language Leaf",
            category = "Other",
            durationDays = 30,
            subgoals = dailyTasks("Learn 5 new words daily", 30),
            imageRes = R.drawable.language,
        ),
    )

    private fun dailyTasks(description: String, days: Int): List<String> =
        List(days) { index -> "$description (day ${index + 1})" }
}
