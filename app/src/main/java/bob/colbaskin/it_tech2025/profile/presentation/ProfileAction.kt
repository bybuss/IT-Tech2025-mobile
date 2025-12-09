package bob.colbaskin.it_tech2025.profile.presentation

sealed interface ProfileAction {
    data object LoadUser: ProfileAction
    data object Logout: ProfileAction
}
