package bob.colbaskin.it_tech2025.auth.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.it_tech2025.auth.domain.auth.AuthRepository
import bob.colbaskin.it_tech2025.common.UiState
import bob.colbaskin.it_tech2025.common.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class EmailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var state by mutableStateOf(EmailInputState())
        private set

    fun onAction(action: EmailInputAction) {
        when (action) {
            is EmailInputAction.UpdateEmail -> updateEmail(action.email)
            is EmailInputAction.LoginByEmail -> loginByEmail(action.email)
            else -> Unit
        }
    }

    fun resetAuthState() {
        state = state.copy(authState = UiState.Loading)
    }

    private fun loginByEmail(email: String) {
        viewModelScope.launch {
            state = state.copy(authState = UiState.Loading)
            val response = authRepository.login(email).toUiState()
            state = state.copy(authState = response)
        }
    }

    private fun updateEmail(newEmail: String) {
        state = state.copy(email = newEmail)
    }
}
