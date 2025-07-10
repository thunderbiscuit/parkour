package org.bitcoinopentools.parkour.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = White,
    secondary = LightGray,
    tertiary = MediumGray,
    background = Black,
    surface = DarkGray,
    onPrimary = Black,
    onSecondary = Black,
    onTertiary = Black,
    onBackground = White,
    onSurface = White,
    primaryContainer = LightGray,
)

private val LightColorScheme = lightColorScheme(
    primary = Black,
    secondary = MediumGray,
    tertiary = DarkGray,
    background = White,
    surface = LightGray,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Black,
    onSurface = Black,
    primaryContainer = LightGray,
    onPrimaryContainer = Black
)

@Composable
fun ParkourTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme =LightColorScheme
    // val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
