package com.rick.ecatalogs.data.ui_components.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rick.ecatalogs.data.ui_design.R

/** High Tower, the brand serif used for titles across XML and Compose. */
val EcsSerif = FontFamily(Font(R.font.high_tower_text))

/**
 * The app's Compose theme. Colors come from the same `data_ui_design_*` resources as the XML
 * theme (values-night is the dark, default look), so Compose screens match the rest of the app.
 * Night mode follows AppCompatDelegate, which is dark unless the user picked Light in Settings.
 */
@Composable
fun EcsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val primary = colorResource(R.color.data_ui_design_color_buttons)
    val onPrimary = colorResource(R.color.data_ui_design_on_primary)
    val background = colorResource(R.color.data_ui_design_color_surface)
    val container = colorResource(R.color.data_ui_design_color_on_surface)
    val containerHigh = colorResource(R.color.data_ui_design_chip_background)
    val onSurface = colorResource(R.color.data_ui_design_black)
    val onSurfaceVariant = colorResource(R.color.data_ui_design_text_secondary)
    val outline = colorResource(R.color.data_ui_design_outline)

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primary, onPrimary = onPrimary,
            primaryContainer = containerHigh, onPrimaryContainer = onSurface,
            secondary = primary, onSecondary = onPrimary,
            secondaryContainer = containerHigh, onSecondaryContainer = onSurface,
            background = background, onBackground = onSurface,
            surface = background, onSurface = onSurface,
            surfaceVariant = container, onSurfaceVariant = onSurfaceVariant,
            surfaceContainerLowest = background, surfaceContainerLow = container,
            surfaceContainer = container, surfaceContainerHigh = containerHigh,
            surfaceContainerHighest = containerHigh,
            outline = outline, outlineVariant = outline,
        )
    } else {
        lightColorScheme(
            primary = primary, onPrimary = onPrimary,
            primaryContainer = containerHigh, onPrimaryContainer = onSurface,
            secondary = primary, onSecondary = onPrimary,
            secondaryContainer = containerHigh, onSecondaryContainer = onSurface,
            background = background, onBackground = onSurface,
            surface = background, onSurface = onSurface,
            surfaceVariant = container, onSurfaceVariant = onSurfaceVariant,
            surfaceContainerLowest = background, surfaceContainerLow = container,
            surfaceContainer = container, surfaceContainerHigh = containerHigh,
            surfaceContainerHighest = containerHigh,
            outline = outline, outlineVariant = outline,
        )
    }

    MaterialTheme(colorScheme = colorScheme, typography = EcsTypography) {
        // Text outside a Surface would otherwise default to black (unreadable on the dark theme)
        CompositionLocalProvider(LocalContentColor provides colorScheme.onSurface, content = content)
    }
}

/** Serif for display/headline/title, system sans for body and labels. */
val EcsTypography: Typography = Typography().run {
    copy(
        displayLarge = displayLarge.serif(),
        displayMedium = displayMedium.serif(),
        displaySmall = displaySmall.serif(),
        headlineLarge = headlineLarge.serif(),
        headlineMedium = headlineMedium.serif(),
        headlineSmall = headlineSmall.serif(),
        titleLarge = titleLarge.serif().copy(fontSize = 22.sp),
        titleMedium = titleMedium.serif().copy(fontSize = 18.sp),
        titleSmall = titleSmall.copy(fontWeight = FontWeight.Medium),
    )
}

private fun TextStyle.serif() = copy(fontFamily = EcsSerif, fontWeight = FontWeight.Bold)
