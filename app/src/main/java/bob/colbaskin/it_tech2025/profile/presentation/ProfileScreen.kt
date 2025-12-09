package bob.colbaskin.it_tech2025.profile.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import bob.colbaskin.it_tech2025.common.UiState
import bob.colbaskin.it_tech2025.common.design_system.ErrorScreen
import bob.colbaskin.it_tech2025.common.design_system.LoadingScreen
import bob.colbaskin.it_tech2025.profile.domain.models.User

@Composable
fun ProfileScreenRoot(
    viewModel: ProfileViewModel = hiltViewModel()
) {

    val state = viewModel.state

    ProfileScreen(
        state = state,
        onAction = { action ->
            when (action) {
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit
) {
    when (val user = state.userState) {
        is UiState.Error -> {
            ErrorScreen(
                message = user.text,
                onError = { onAction(ProfileAction.LoadUser) }
            )
        }
        UiState.Loading -> {
            LoadingScreen(
                onError = { onAction(ProfileAction.LoadUser) }
            )
        }
        is UiState.Success<User> -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    Text(text = user.data.userId.toString())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = user.data.email)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onAction(ProfileAction.Logout) }
                    ) {
                        Text(text = "Logout")
                    }
                }
            }
        }
    }
}
