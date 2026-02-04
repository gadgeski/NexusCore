package com.gadgeski.nexuscore.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gadgeski.nexuscore.ui.theme.Vermilion
import com.gadgeski.nexuscore.ui.theme.NeonPurple
import kotlin.random.Random

@Composable
fun NoiseBackground(
    modifier: Modifier = Modifier,
    scanlineColor: Color = Vermilion,
    particleColor: Color = NeonPurple
) {
    val infiniteTransition = rememberInfiniteTransition(label = "noise_anim")

    // ノイズの揺らぎ（視認性ブースト維持）
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val scanlineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val gap = 4.dp.toPx()
        var y = -scanlineOffset

        while (y < height) {
            drawRect(
                color = scanlineColor.copy(alpha = 0.3f),
                topLeft = Offset(0f, y),
                size = Size(width, 1f)
            )
            y += gap
        }

        repeat(80) {
            val randomX = Random.nextFloat() * width
            val randomY = Random.nextFloat() * height
            val randomWidth = Random.nextFloat() * 20.dp.toPx()
            val randomHeight = 2.dp.toPx()

            // 【変更】指定色とアクセント色をランダムに混ぜる
            val noiseColor = if (Random.nextBoolean()) scanlineColor else particleColor

            drawRect(
                color = noiseColor,
                topLeft = Offset(randomX, randomY),
                size = Size(randomWidth, randomHeight),
                alpha = alpha,
                blendMode = BlendMode.Screen
            )
        }
    }
}