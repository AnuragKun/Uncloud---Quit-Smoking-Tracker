package com.arlabs.uncloud.presentation.theme

import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
        darkColorScheme(
            primary = CyberCyan,
            onPrimary = Color.Black,
            secondary = CyberGreen,
            onSecondary = Color.Black,
            tertiary = CyberAmber,
            background = CyberDark,
            onBackground = Color.White,
            surface = CyberPanel,
            onSurface = Color.White,
            error = CyberRed,
            outline = CyberBorder,
            outlineVariant = CyberBorder
        )

// Note: The app is designed to be Always Dark (Terminal Theme).
// We map the same colors to LightColorScheme to enforce consistency even if system is Light.
private val LightColorScheme =
        lightColorScheme(
            primary = CyberCyan,
            onPrimary = Color.Black,
            secondary = CyberGreen,
            onSecondary = Color.Black,
            tertiary = CyberAmber,
            background = CyberDark,
            onBackground = Color.White,
            surface = CyberPanel,
            onSurface = Color.White,
            error = CyberRed,
            outline = CyberBorder,
            outlineVariant = CyberBorder
        )

@Composable
fun QuitSmokingTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        // Dynamic color is available on Android 12+
        // We DEFAULT TO FALSE to enforce the Cyberpunk aesthetic over system wallpaper colors
        dynamicColor: Boolean = false, 
        content: @Composable () -> Unit
) {
    val colorScheme =
            when {
                // Logic kept if user explicitly enables it in code, but default is OFF.
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context)
                    else dynamicLightColorScheme(context)
                }
                // Since we mapped both to the same palette, this switch is technically redundant 
                // but good for structure if we ever want a distinct "Light Mode" terminal.
                darkTheme -> DarkColorScheme
                else -> LightColorScheme
            }

    val view = LocalContext.current // Wait, LocalView is better for WindowCompat
    // Actually, checking implementation of LocalView
    val localView = androidx.compose.ui.platform.LocalView.current
    if (!localView.isInEditMode) {
        androidx.compose.runtime.SideEffect {
            val window = (localView.context as android.app.Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            androidx.core.view.WindowCompat.getInsetsController(window, localView).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
