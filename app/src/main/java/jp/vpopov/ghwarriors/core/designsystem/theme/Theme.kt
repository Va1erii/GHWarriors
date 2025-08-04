package jp.vpopov.ghwarriors.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SoftAccentBlue,
    onPrimary = Color.White,
    primaryContainer = SoftAccentBlue.copy(alpha = 0.2f),
    onPrimaryContainer = Color.White,
    secondary = WarmGray300,
    onSecondary = Color.Black,
    background = WarmGray900,
    onBackground = Color.White,
    surface = WarmGray800,
    onSurface = Color.White,
    surfaceVariant = WarmGray800.copy(alpha = 0.8f),
    onSurfaceVariant = Color.White.copy(alpha = 0.8f),
    tertiary = SoftPurple,
    onTertiary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = SoftAccentBlue,
    onPrimary = Color.White,
    primaryContainer = SoftAccentBlue.copy(alpha = 0.1f),
    onPrimaryContainer = Color.Black,
    secondary = WarmGray300,
    onSecondary = Color.Black,
    background = WarmWhite,
    onBackground = Color.Black,
    surface = WarmSurface,
    onSurface = Color.Black,
    surfaceVariant = WarmGray300.copy(alpha = 0.5f),
    onSurfaceVariant = Color.Black.copy(alpha = 0.6f),
    tertiary = SoftPink,
    onTertiary = Color.White
)

@Composable
fun GHWarriorsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}