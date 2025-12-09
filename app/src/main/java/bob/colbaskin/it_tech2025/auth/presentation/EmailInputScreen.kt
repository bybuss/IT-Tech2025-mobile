package bob.colbaskin.it_tech2025.auth.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import bob.colbaskin.it_tech2025.common.UiState
import bob.colbaskin.it_tech2025.navigation.Screens

@Composable
fun EmailScreenRoot(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: EmailViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val authState = state.authState

    LaunchedEffect(authState) {
        when (authState) {
            is UiState.Error -> {
                snackbarHostState.showSnackbar(
                    authState.title,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetAuthState()
            }
            else -> {}
        }
    }

    EmailInputContent(
        state = state,
        onAction = { action ->
            when (action) {
                is EmailInputAction.NavigateToOTPScreen -> {
                    navController.navigate(Screens.OTPScreen(state.email))
                }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmailInputContent(
    state: EmailInputState,
    onAction: (EmailInputAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 30.dp)
            .imePadding()
            .imeNestedScroll()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = state.email,
                onValueChange = { newEmail ->
                    onAction(EmailInputAction.UpdateEmail(newEmail))
                },
                label = { Text("Введите email") },
                placeholder = { Text("youremail@gmail.com") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imeNestedScroll(),
                isError = !state.isValid && state.email.isNotEmpty(),
                trailingIcon = {
                    if (!state.isValid && state.email.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "error",
                            tint = Color.Red
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray,
                    disabledContainerColor = Color.LightGray
                )
            )
        }
        IconButton(
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.BottomEnd)
                .size(70.dp)
                .clip(RoundedCornerShape(50)),
            onClick = {
                onAction(EmailInputAction.LoginByEmail(state.email))
                if (state.authState is UiState.Success<*>) {
                    onAction(EmailInputAction.NavigateToOTPScreen(email = state.email))
                }
            },
            enabled = state.isValid,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Magenta,
                disabledContainerColor = Color.Magenta.copy(alpha = 0.3f)
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "rightArrow"
            )
        }
    }
}
