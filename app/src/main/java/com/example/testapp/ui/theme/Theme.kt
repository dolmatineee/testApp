package com.example.testapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0077FF), // Новый основной цвет
    onPrimary = Color(0xFFFFFFFF), // Цвет текста на основном цвете
    primaryContainer = Color(0xFF0047A3), // Контейнер с основным цветом
    onPrimaryContainer = Color(0xFFD1E4FF), // Цвет текста на контейнере с основным цветом
    secondary = Color(0xFFB2C8FF), // Вторичный цвет (сочетается с основным)
    onSecondary = Color(0xFF002C7A), // Цвет текста на вторичном цвете
    secondaryContainer = Color(0xFF1A3F8F), // Контейнер с вторичным цветом
    onSecondaryContainer = Color(0xFFD7E2FF), // Цвет текста на контейнере с вторичным цветом
    tertiary = Color(0xFFA8C8FF), // Третичный цвет (сочетается с основным)
    onTertiary = Color(0xFF0A3445), // Цвет текста на третичном цвете
    tertiaryContainer = Color(0xFF264B5C), // Контейнер с третичным цветом
    onTertiaryContainer = Color(0xFFC4E7FF), // Цвет текста на контейнере с третичным цветом
    error = Color(0xFFBA1A1A), // Цвет ошибки
    errorContainer = Color(0xFFFFDAD6), // Контейнер с цветом ошибки
    onError = Color(0xFF690005), // Цвет текста на цвете ошибки
    onErrorContainer = Color(0xFF410002), // Цвет текста на контейнере с цветом ошибки
    background = Color(0xFF101010), // Цвет фона
    onBackground = Color(0xFFE0E3E1), // Цвет текста на фоне
    surface = Color(0xFF1A1B1C), // Цвет поверхности
    onSurface = Color(0xFFE0E2E3), // Цвет текста на поверхности
    surfaceVariant = Color(0xFF3F4649), // Вариант цвета поверхности
    onSurfaceVariant = Color(0xFFBEC9C7), // Цвет текста на варианте поверхности
    outline = Color(0xFF797979), // Цвет контура
    inverseOnSurface = Color(0xFF1F8A4B), // Инвертированный цвет текста на поверхности
    inverseSurface = Color(0xFF04C14D), // Инвертированный цвет поверхности
    inversePrimary = Color(0xFF006A66), // Инвертированный основной цвет
    surfaceTint = Color(0xFF0077FF), // Цвет оттенка поверхности
    outlineVariant = Color(0xFF3F4249), // Вариант цвета контура
    scrim = Color(0xFF000000), // Цвет затемнения
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0077FF), // Новый основной цвет
    onPrimary = Color(0xFFFFFFFF), // Цвет текста на основном цвете
    primaryContainer = Color(0xFFD1E4FF), // Контейнер с основным цветом
    onPrimaryContainer = Color(0xFF001A40), // Цвет текста на контейнере с основным цветом
    secondary = Color(0xFF4A637C), // Вторичный цвет (сочетается с основным)
    onSecondary = Color(0xFFFFFFFF), // Цвет текста на вторичном цвете
    secondaryContainer = Color(0xFFD0E4FF), // Контейнер с вторичным цветом
    onSecondaryContainer = Color(0xFF031E30), // Цвет текста на контейнере с вторичным цветом
    tertiary = Color(0xFF4A637C), // Третичный цвет (сочетается с основным)
    onTertiary = Color(0xFFFFFFFF), // Цвет текста на третичном цвете
    tertiaryContainer = Color(0xFFD0E4FF), // Контейнер с третичным цветом
    onTertiaryContainer = Color(0xFF031E30), // Цвет текста на контейнере с третичным цветом
    error = Color(0xFFBA1A1A), // Цвет ошибки
    errorContainer = Color(0xFFFFDAD6), // Контейнер с цветом ошибки
    onError = Color(0xFFFFFFFF), // Цвет текста на цвете ошибки
    onErrorContainer = Color(0xFF410002), // Цвет текста на контейнере с цветом ошибки
    background = Color(0xFFFFFFFF), // Цвет фона
    onBackground = Color(0xFF101010), // Цвет текста на фоне
    surface = Color(0xFFF2F3F5), // Цвет поверхности
    onSurface = Color(0xFF171D1C), // Цвет текста на поверхности
    surfaceVariant = Color(0xFFD0D3E1), // Вариант цвета поверхности
    onSurfaceVariant = Color(0xFF3F4948), // Цвет текста на варианте поверхности
    outline = Color(0xFF797979), // Цвет контура
    inverseOnSurface = Color(0xFFEBEEEC), // Инвертированный цвет текста на поверхности
    inverseSurface = Color(0xFF2B3231), // Инвертированный цвет поверхности
    inversePrimary = Color(0xFF9ECAFF), // Инвертированный основной цвет
    surfaceTint = Color(0xFF0077FF), // Цвет оттенка поверхности
    outlineVariant = Color(0xFFBEC3C9), // Вариант цвета контура
    scrim = Color(0xFF000000) // Цвет затемнения
)

@Composable
fun TestAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}