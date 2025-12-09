package bob.colbaskin.it_tech2025.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.it_tech2025.common.toUiState
import bob.colbaskin.it_tech2025.common.user_prefs.data.models.AuthConfig
import bob.colbaskin.it_tech2025.common.user_prefs.domain.UserPreferencesRepository
import bob.colbaskin.it_tech2025.di.token.TokenManager
import bob.colbaskin.it_tech2025.profile.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val tokenManager: TokenManager
): ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    init {
        loadUser()
    }
    
    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.LoadUser -> loadUser()
            ProfileAction.Logout -> logout()
            else -> Unit
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = profileRepository.getUser()
            state = state.copy(
                userState = user.toUiState()
            )
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.saveAuthStatus(AuthConfig.NOT_AUTHENTICATED)
            tokenManager.cleatTokens()
        }
    }
}
