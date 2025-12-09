package bob.colbaskin.it_tech2025.auth.presentation

sealed interface EmailInputAction {
    data class NavigateToOTPScreen(val email: String): EmailInputAction
    data class UpdateEmail(val email: String): EmailInputAction
    data class LoginByEmail(val email: String): EmailInputAction
}