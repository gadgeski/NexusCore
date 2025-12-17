package com.gadgeski.abbozzo.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import com.gadgeski.abbozzo.ui.theme.BlackBackground
import com.gadgeski.abbozzo.ui.theme.NeonCyan
import com.gadgeski.abbozzo.ui.theme.NeonPurple
import kotlin.random.Random

@Composable
fun NoiseBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(com.gadgeski.abbozzo.ui.theme.DarkRedBlack)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Base Gradient (Red Industrial - HOTTER)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        com.gadgeski.abbozzo.ui.theme.DarkRedBlack,
                        com.gadgeski.abbozzo.ui.theme.DarkRedSurface, 
                        Color(0xFF200505) // Brighter bottom
                    )
                )
            )

            // 2. Heat Source (Radial Gradient - INTENSE)
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        com.gadgeski.abbozzo.ui.theme.Vermilion.copy(alpha = 0.3f), // Core heat boosted
                        com.gadgeski.abbozzo.ui.theme.DarkRedBlack.copy(alpha = 0f)
                    ),
                    center = center,
                    radius = width * 0.9f // Wider radius
                )
            )

            // 3. Scanlines (CRT Effect) - VERY VISIBLE
            val lineSpacing = 5f // Tighter lines
            for (y in 0 until height.toInt() step lineSpacing.toInt()) {
                drawLine(
                    color = Color.Black.copy(alpha = 0.8f), // Darker lines for contrast
                    start = Offset(0f, y.toFloat()),
                    end = Offset(width, y.toFloat()),
                    strokeWidth = 2f
                )
            }

            // 4. Digital Noise / Grain (CHAOTIC)
            val points = List(15000) { // Massive point count increase
                Offset(
                    x = androidx.compose.ui.util.lerp(0f, width, Random.nextFloat()),
                    y = androidx.compose.ui.util.lerp(0f, height, Random.nextFloat())
                )
            }
            
            drawPoints(
                points = points,
                pointMode = PointMode.Points,
                color = com.gadgeski.abbozzo.ui.theme.Vermilion.copy(alpha = 0.45f), // Very visible red grain
                strokeWidth = 2f
            )

            // 5. Color Glitches (FREQUENT & BRIGHT)
            repeat(8) { // More glitches
                val y = Random.nextFloat() * height
                drawLine(
                    color = if (Random.nextBoolean()) com.gadgeski.abbozzo.ui.theme.Vermilion.copy(alpha = 0.8f) else com.gadgeski.abbozzo.ui.theme.MagmaOrange.copy(alpha = 0.8f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = if (Random.nextBoolean()) 1f else 3f // Variable width
                )
            }
            
            // 6. Vigilante Vignette (Dark corners)
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f)),
                    center = center,
                    radius = width * 0.9f
                ),
                blendMode = BlendMode.DstOut
            )
        }
    }
}
