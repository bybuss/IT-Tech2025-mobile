package bob.colbaskin.it_tech2025.navigation

import kotlinx.serialization.Serializable

interface Screens {
    @Serializable
    data object ScannerScreen: Screens

    @Serializable
    data object SomeScreen: Screens

    @Serializable
    data object Profile: Screens

    @Serializable
    data object Welcome: Screens

    @Serializable
    data object Introduction: Screens

    @Serializable
    data object EmailInput: Screens

    @Serializable
    data class OTPScreen(val email: String): Screens
}
