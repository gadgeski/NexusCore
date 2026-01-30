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
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "noise_anim")

    // アルファ値を全体的に底上げ (0.02f -> 0.15f程度まで上げる)
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.15f,
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

        // 1. Scanlines (走査線)
        // 修正点: 黒背景で見えるように「白」に変更し、アルファ値を調整
        val gap = 4.dp.toPx()
        var y = -scanlineOffset

        while (y < height) {
            drawRect(
                color = Color.White.copy(alpha = 0.03f), // 【修正】白く薄く光らせる
                topLeft = Offset(0f, y),
                size = Size(width, 1f)
            )
            y += gap
        }

        // 2. Random Grain Noise (粒子ノイズ)
        repeat(80) { // 【修正】粒子数を50 -> 80に増加
            val randomX = Random.nextFloat() * width
            val randomY = Random.nextFloat() * height
            // 【修正】サイズを少し大きくして視認性アップ
            val randomWidth = Random.nextFloat() * 4.dp.toPx()
            val randomHeight = 1.dp.toPx()

            // 色をランダムに変化（朱色 or ネオンパープル）
            val noiseColor = if (Random.nextBoolean()) Vermilion else NeonPurple

            drawRect(
                color = noiseColor,
                topLeft = Offset(randomX, randomY),
                size = Size(randomWidth, randomHeight),
                alpha = alpha * Random.nextFloat(),
                blendMode = BlendMode.Screen // 黒背景に加算合成
            )
        }
    }
}