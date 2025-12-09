package bob.colbaskin.it_tech2025.common.user_prefs.domain

import bob.colbaskin.it_tech2025.common.user_prefs.data.models.AuthConfig
import bob.colbaskin.it_tech2025.common.user_prefs.data.models.OnboardingConfig
import bob.colbaskin.it_tech2025.common.user_prefs.data.models.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getUserPreferences(): Flow<UserPreferences>
    suspend fun saveAuthStatus(status: AuthConfig)
    suspend fun saveOnboardingStatus(status: OnboardingConfig)
}
