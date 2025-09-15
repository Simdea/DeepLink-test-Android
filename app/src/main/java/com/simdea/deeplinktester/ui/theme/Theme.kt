package com.simdea.deeplinktester.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Yellow,
    background = DarkBackground,
    surface = DarkCardBackground,
    onPrimary = TextBlack,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    secondary = TextGray, // Example, can be refined
    tertiary = Red, // Example
)

private val LightColorScheme = lightColorScheme(
    primary = Yellow,
    background = BackgroundGray,
    surface = White,
    onPrimary = TextBlack,
    onBackground = TextBlack,
    onSurface = TextBlack,
    secondary = TextGray,
    tertiary = Red,
)

@Composable
fun DeepLinkTestAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
