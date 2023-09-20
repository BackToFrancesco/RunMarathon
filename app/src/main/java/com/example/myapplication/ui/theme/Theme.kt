package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val DarkColorPalette = darkColors(
    primary = Blue,
    secondary = Yellow,
    background = Dark,
    surface = Dark,
    onPrimary = Dark,
    onSecondary = Dark,
    onBackground = Light,
    onSurface = Light
)

private val LightColorPalette = lightColors(
    primary = Blue,
    secondary = Yellow,
    background = Light,
    surface = Light,
    onPrimary = Light,
    onSecondary = Light,
    onBackground = Dark,
    onSurface = Dark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    //val colors = if (darkTheme) DarkColorPalette else LightColorPalette
    val colors = LightColorPalette
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}