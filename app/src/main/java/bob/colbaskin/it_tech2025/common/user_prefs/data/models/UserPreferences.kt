package bob.colbaskin.it_tech2025.common.user_prefs.data.models

import bob.colbaskin.datastore.OnboardingStatus
import bob.colbaskin.hack.datastore.AuthStatus
import bob.colbaskin.webantpractice.datastore.UserPreferencesProto

data class UserPreferences(
    val onboardingStatus: OnboardingConfig,
    val authStatus: AuthConfig
)

fun UserPreferencesProto.toData(): UserPreferences {
    return UserPreferences(
        onboardingStatus = when (this.onboardingStatus) {
            OnboardingStatus.NOT_STARTED -> OnboardingConfig.NOT_STARTED
            OnboardingStatus.IN_PROGRESS -> OnboardingConfig.IN_PROGRESS
            OnboardingStatus.COMPLETED -> OnboardingConfig.COMPLETED
            OnboardingStatus.UNRECOGNIZED, null -> OnboardingConfig.NOT_STARTED
        },
        authStatus = when (this.authStatus) {
            AuthStatus.AUTHENTICATED -> AuthConfig.AUTHENTICATED
            AuthStatus.NOT_AUTHENTICATED -> AuthConfig.NOT_AUTHENTICATED
            AuthStatus.UNRECOGNIZED, null -> AuthConfig.NOT_AUTHENTICATED
        }
    )
}
