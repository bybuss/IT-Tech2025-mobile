package bob.colbaskin.it_tech2025.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destinations(
    val icon: ImageVector,
    val label: String,
    val screen: Screens
) {
    SCANNER(
        icon = Icons.Default.QrCodeScanner,
        label = "Home",
        screen = Screens.ScannerScreen
    ),
    SOME_SCREEN(
        icon = Icons.Default.Screenshot,
        label = "SomeScreen",
        screen = Screens.SomeScreen
    ),
    PROFILE(
        icon = Icons.Default.Person,
        label = "Profile",
        screen = Screens.Profile
    )
}
