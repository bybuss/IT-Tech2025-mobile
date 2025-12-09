package bob.colbaskin.it_tech2025.profile.presentation

import bob.colbaskin.it_tech2025.common.UiState
import bob.colbaskin.it_tech2025.profile.domain.models.User

data class ProfileState (
    val userState: UiState<User> = UiState.Loading
)
