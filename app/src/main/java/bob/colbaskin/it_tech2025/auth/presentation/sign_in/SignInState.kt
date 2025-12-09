package bob.colbaskin.it_tech2025.auth.presentation.sign_in

import android.util.Patterns
import bob.colbaskin.it_tech2025.common.UiState

data class SignInState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val authState: UiState<Unit> = UiState.Loading
) {
    val isEmailValid: Boolean
        get() = email.isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
