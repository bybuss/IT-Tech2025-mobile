package bob.colbaskin.it_tech2025.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListBulleted
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
        label = "Сканер",
        screen = Screens.ScannerScreen
    ),
    VERIFICATION_LOG(
        icon = Icons.Default.FormatListBulleted,
        label = "Журнал Валидаций",
        screen = Screens.VerificationLogScreen
    ),
    PROFILE(
        icon = Icons.Default.Person,
        label = "Профиль",
        screen = Screens.Profile
    )
}
