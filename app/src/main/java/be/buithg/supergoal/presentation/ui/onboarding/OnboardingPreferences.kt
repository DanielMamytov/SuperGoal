package be.buithg.supergoal.presentation.ui.onboarding

import android.content.Context

internal const val PREFS_NAME = "super_goal_prefs"
internal const val KEY_HAS_SEEN_ONBOARDING = "has_seen_onboarding"

internal fun Context.hasSeenOnboarding(): Boolean =
    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(KEY_HAS_SEEN_ONBOARDING, false)

internal fun Context.markOnboardingSeen() {
    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(KEY_HAS_SEEN_ONBOARDING, true)
        .apply()
}
