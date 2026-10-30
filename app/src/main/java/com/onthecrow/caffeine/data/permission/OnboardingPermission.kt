package com.onthecrow.caffeine.data.permission

/**
 * Permissions / actions surfaced during onboarding, ordered by descending criticality.
 *
 * [OVERLAY] is the only one truly required for the core screen-awake feature; the rest
 * improve the experience but the app keeps working without them.
 */
enum class OnboardingPermission {
    OVERLAY,
    NOTIFICATIONS,
    BATTERY_OPTIMIZATION,
    QUICK_TILE,
}
