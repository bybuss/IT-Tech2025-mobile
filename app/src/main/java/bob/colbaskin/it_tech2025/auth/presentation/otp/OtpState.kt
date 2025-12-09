package bob.colbaskin.it_tech2025.auth.presentation.otp

import bob.colbaskin.it_tech2025.common.UiState

data class OtpState(
    val authState: UiState<Unit> = UiState.Loading,
    val code: List<Int?> = (1..4).map { null },
    val focusedIndex: Int? = null,
    val isValid: Boolean? = null,
    val shouldNavigateNext: Boolean = false,
    val email: String = ""
)
