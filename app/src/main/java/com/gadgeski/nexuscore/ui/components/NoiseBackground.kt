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
import androidx.compose.ui.unit.dp
import com.gadgeski.nexuscore.ui.theme.Vermilion
import com.gadgeski.nexuscore.ui.theme.NeonPurple
import kotlin.random.Random

@Composable
fun NoiseBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "noise_anim")

    // 【変更点1】 ノイズの基準不透明度を「0.5〜0.8」まで引き上げ
    // これなら確実に「濃い色」として見えます
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
                // 【変更点2】 走査線を「0.3f (30%)」まで強化
                // 黒背景の上でもはっきりと「暗い赤の縞模様」が見えるはずです
                color = Vermilion.copy(alpha = 0.3f),
                topLeft = Offset(0f, y),
                size = Size(width, 1f)
            )
            y += gap
        }

        repeat(80) {
            val randomX = Random.nextFloat() * width
            val randomY = Random.nextFloat() * height

            // 【変更点3】 粒子のサイズを巨大化 (横幅最大 20dp)
            // これで「点」ではなく「線状のノイズ」として認識しやすくします
            val randomWidth = Random.nextFloat() * 20.dp.toPx()
            val randomHeight = 2.dp.toPx() // 高さも倍増

            val noiseColor = if (Random.nextBoolean()) Vermilion else NeonPurple

            drawRect(
                color = noiseColor,
                topLeft = Offset(randomX, randomY),
                size = Size(randomWidth, randomHeight),
                // アルファ値のランダム減衰を廃止し、そのまま適用
                alpha = alpha,
                blendMode = BlendMode.Screen
            )
        }
    }
}