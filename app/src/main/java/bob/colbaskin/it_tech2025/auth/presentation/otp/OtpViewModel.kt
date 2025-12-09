package bob.colbaskin.it_tech2025.auth.presentation.otp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.it_tech2025.auth.domain.auth.AuthRepository
import bob.colbaskin.it_tech2025.common.UiState
import bob.colbaskin.it_tech2025.common.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var state by mutableStateOf(OtpState())
        private set

    fun onAction(action: OtpAction) {
        when(action) {
            is OtpAction.OnChangeFieldFocused -> state = state.copy(focusedIndex = action.index)
            is OtpAction.OnEnterNumber -> enterNumber(action.number, action.index)
            OtpAction.OnKeyboardBack -> {
                val previousIndex = getPreviousFocusedIndex(state.focusedIndex)
                state = state.copy(
                    code = state.code.mapIndexed { index, number ->
                        if(index == previousIndex) {
                            null
                        } else {
                            number
                        }
                    },
                    focusedIndex = previousIndex
                )
            }
            is OtpAction.LoginWithCode -> loginWithCode(action.code)
            else -> Unit
        }
    }

    fun resetAuthState() {
        state = state.copy(authState = UiState.Loading)
    }

    fun saveEmail(email: String) {
        state = state.copy(email = email)
    }

    private fun loginWithCode(code: String) {
        viewModelScope.launch {
            state = state.copy(authState = UiState.Loading)
            val response = authRepository.confirmLogin(code = code, email = state.email).toUiState()
            state = state.copy(authState = response)
        }
    }

    private fun enterNumber(number: Int?, index: Int) {
        Log.d("Logging", "Entering number: $number at index: $index")
        val newCode = state.code.mapIndexed { currentIndex, currentNumber ->
            if(currentIndex == index) {
                number
            } else {
                currentNumber
            }
        }
        val wasNumberRemoved = number == null
        state = state.copy(
            code = newCode,
            focusedIndex = if(wasNumberRemoved || state.code.getOrNull(index) != null) {
                state.focusedIndex
            } else {
                getNextFocusedTextFieldIndex(
                    currentCode = state.code,
                    currentFocusedIndex = state.focusedIndex
                )
            },
            isValid = null
        )

        if(newCode.none { it == null }) {
            viewModelScope.launch {
                val codeStr = newCode.joinToString("")
                loginWithCode(codeStr)
            }
        }
    }

    private fun getPreviousFocusedIndex(currentIndex: Int?): Int? {
        return currentIndex?.minus(1)?.coerceAtLeast(0)
    }

    private fun getNextFocusedTextFieldIndex(
        currentCode: List<Int?>,
        currentFocusedIndex: Int?
    ): Int? {
        if(currentFocusedIndex == null) {
            return null
        }

        if(currentFocusedIndex == 3) {
            return currentFocusedIndex
        }

        return getFirstEmptyFieldIndexAfterFocusedIndex(
            code = currentCode,
            currentFocusedIndex = currentFocusedIndex
        )
    }

    private fun getFirstEmptyFieldIndexAfterFocusedIndex(
        code: List<Int?>,
        currentFocusedIndex: Int
    ): Int {
        code.forEachIndexed { index, number ->
            if(index <= currentFocusedIndex) {
                return@forEachIndexed
            }
            if(number == null) {
                return index
            }
        }
        return currentFocusedIndex
    }
}
