package bob.colbaskin.it_tech2025.common.design_system.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color


data class AppColors(
    val black: Color,
    val white: Color,
    val glass: Color,
    val background: Color,
    val cardBackground: Color,
)

val LocalColors = compositionLocalOf { lightColors }

val lightColors = AppColors(
    black = Color(0xFF000000),
    white = Color(0xFFFFFFFF),
    glass = Color(0xFFFFFFFF).copy(alpha = 0.3f),
    background = Color(0xFFFAFAF9),
    cardBackground = Color(0xFF000000),
)

val darkColors  = AppColors(
    black = Color(0xFF000000),
    white = Color(0xFFFFFFFF),
    glass = Color(0xFFFFFFFF).copy(alpha = 0.3f),
    background = Color(0xFF1D1D1D),
    cardBackground = Color(0xFFE3E3E3),
)
