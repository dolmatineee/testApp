package com.example.testapp.ui.customs


import android.annotation.SuppressLint
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

@SuppressLint("SuspiciousModifierThen")
fun Modifier.shimmer(
    colors: List<Color> = ShimmerColorShades,
    durationMillis: Int = 1200,
    easing: Easing = FastOutSlowInEasing
): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = easing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val brush = remember(translateAnim) {
        Brush.linearGradient(
            colors = colors,
            start = Offset(10f, 10f),
            end = Offset(translateAnim, translateAnim)
        )
    }

    this.then(
        drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(brush = brush)
            }
        }
    )
}

private val ShimmerColorShades = listOf(
    Color.LightGray.copy(alpha = 0.9f),
    Color.LightGray.copy(alpha = 0.2f),
    Color.LightGray.copy(alpha = 0.9f)
)