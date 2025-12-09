package bob.colbaskin.it_tech2025.auth.presentation.otp

sealed interface OtpAction {
    data class OnEnterNumber(val number: Int?, val index: Int): OtpAction
    data class OnChangeFieldFocused(val index: Int): OtpAction
    data object OnKeyboardBack: OtpAction
    data object NavigateMain: OtpAction
    data class LoginWithCode(val code: String): OtpAction
}
