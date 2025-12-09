package bob.colbaskin.it_tech2025.auth.presentation

import bob.colbaskin.it_tech2025.common.UiState
import java.util.regex.Pattern

data class EmailInputState(
    val authState: UiState<Unit> = UiState.Loading,
    val email: String = "",
) {
    fun validateEmail(): Boolean = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    ).matcher(this.email).matches()

    val isValid: Boolean = validateEmail()
}
