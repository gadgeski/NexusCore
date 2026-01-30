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
import kotlin.random.Random

@Composable
fun NoiseBackground(
    modifier: Modifier = Modifier
) {
    // ノイズの不透明度をアニメーションさせて「揺らぎ」を作る
    val infiniteTransition = rememberInfiniteTransition(label = "noise_anim")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.02f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // スキャンラインの位置をゆっくり移動させる（オプション）
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
        // CRTモニターのような横縞を描画
        // Removed unused variable 'lineHeight' to fix warning
        val gap = 4.dp.toPx()
        var y = -scanlineOffset // アニメーションで少し動かす

        while (y < height) {
            drawRect(
                color = Color.Black.copy(alpha = 0.3f),
                topLeft = Offset(0f, y),
                size = Size(width, 1f) // 極細の線
            )
            y += gap
        }

        // 2. Random Grain Noise (粒子ノイズ)
        // 画面全体にランダムなドットを散らす（重くなりすぎない程度に）
        // ※描画負荷を下げるため、簡易的に矩形をランダム配置
        repeat(50) {
            val randomX = Random.nextFloat() * width
            val randomY = Random.nextFloat() * height
            val randomSize = Random.nextFloat() * 3.dp.toPx()

            drawRect(
                color = Vermilion, // テーマカラーの朱色を微かに混ぜる
                topLeft = Offset(randomX, randomY),
                size = Size(randomSize, 1f), // 横長のノイズ
                alpha = alpha * Random.nextFloat(), // 明滅
                blendMode = BlendMode.Screen
            )
        }

        // 3. Vignette (四隅を暗くする) - 簡易実装
        // 没入感を高めるために外周を暗く塗ることは効果的ですが、
        // 今回はシンプルにNoiseだけに留めます。
    }
}