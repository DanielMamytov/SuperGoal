package be.buithg.supergoal.domain.model

/**
 * Represents the category assigned to a goal. Categories mirror the options available in the
 * design specification so UI and data layers can operate on a shared type.
 */
enum class GoalCategory {
    MIND,
    BODY,
    CAREER,
    MONEY,
    SOCIAL,
    OTHER;

    companion object {
        fun fromRaw(value: String): GoalCategory = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true)
        } ?: OTHER
    }
}
