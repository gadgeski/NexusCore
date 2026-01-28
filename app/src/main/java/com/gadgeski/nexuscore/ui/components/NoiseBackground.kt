package com.gadgeski.nexuscore.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.util.lerp
import com.gadgeski.nexuscore.ui.theme.MagmaOrange
import com.gadgeski.nexuscore.ui.theme.Vermilion
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class CircuitPath(
    val path: Path,
    val color: Color
)

@Composable
fun NoiseBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(com.gadgeski.nexuscore.ui.theme.DarkRedBlack)
    ) {
        val circuitGenerator = remember { CircuitGenerator() }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Base Gradient (Red Industrial - HOTTER)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        com.gadgeski.nexuscore.ui.theme.DarkRedBlack,
                        com.gadgeski.nexuscore.ui.theme.DarkRedSurface,
                        Color(0xFF200505) // Brighter bottom
                    )
                )
            )

            // 2. Heat Source (Radial Gradient - INTENSE)
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Vermilion.copy(alpha = 0.3f), // Core heat boosted
                        com.gadgeski.nexuscore.ui.theme.DarkRedBlack.copy(alpha = 0f)
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
                    x = lerp(0f, width, Random.nextFloat()),
                    y = lerp(0f, height, Random.nextFloat())
                )
            }
            
            drawPoints(
                points = points,
                pointMode = PointMode.Points,
                color = Vermilion.copy(alpha = 0.45f), // Very visible red grain
                strokeWidth = 2f
            )

            // 5. Color Glitches (FREQUENT & BRIGHT)
            repeat(8) { // More glitches
                val y = Random.nextFloat() * height
                drawLine(
                    color = if (Random.nextBoolean()) Vermilion.copy(alpha = 0.8f) else MagmaOrange.copy(alpha = 0.8f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = if (Random.nextBoolean()) 1f else 3f // Variable width
                )
            }

            // 6. CIRCUIT PATTERNS (NEW LAYER)
            val paths = circuitGenerator.getOrGenerate(width, height)
            
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.STROKE
                    strokeCap = android.graphics.Paint.Cap.ROUND
                    strokeJoin = android.graphics.Paint.Join.ROUND
                }

                // First pass: Glow (Thick, Blurred)
                paths.forEach { circuit ->
                    paint.color = circuit.color.toArgb()
                    paint.strokeWidth = 12f
                    paint.alpha = 150
                    paint.maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
                    canvas.nativeCanvas.drawPath(circuit.path.asAndroidPath(), paint)
                }

                // Second pass: Core (Thin, Sharp)
                paint.maskFilter = null
                paint.alpha = 255
                paths.forEach { circuit ->
                    paint.color = Color(0xFFFFCCAA).toArgb() // Bright hot core
                    paint.strokeWidth = 3f
                    canvas.nativeCanvas.drawPath(circuit.path.asAndroidPath(), paint)
                }
            }
            
            // 7. Vigilante Vignette (Dark corners)
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

class CircuitGenerator {
    private var cachedPaths: List<CircuitPath>? = null
    private var lastWidth: Float = 0f
    private var lastHeight: Float = 0f

    fun getOrGenerate(width: Float, height: Float): List<CircuitPath> {
        if (cachedPaths != null && width == lastWidth && height == lastHeight) {
            return cachedPaths!!
        }

        lastWidth = width
        lastHeight = height
        cachedPaths = generatePaths(width, height)
        return cachedPaths!!
    }

    private fun generatePaths(width: Float, height: Float): List<CircuitPath> {
        val paths = mutableListOf<CircuitPath>()
        val count = 12 // Number of traces

        repeat(count) {
            val path = Path()
            // Random start edge
            val side = Random.nextInt(4) // 0: Top, 1: Right, 2: Bottom, 3: Left
            var startX = 0f
            var startY = 0f
            
            when (side) {
                0 -> { startX = Random.nextFloat() * width; startY = 0f }
                1 -> { startX = width; startY = Random.nextFloat() * height }
                2 -> { startX = Random.nextFloat() * width; startY = height }
                3 -> { startX = 0f; startY = Random.nextFloat() * height }
            }
            
            path.moveTo(startX, startY)

            // Move towards center with Manhattan/Diagonal steps
            var currentX = startX
            var currentY = startY
            val targetX = width / 2 + (Random.nextFloat() - 0.5f) * width * 0.4f // Random spot near center
            val targetY = height / 2 + (Random.nextFloat() - 0.5f) * height * 0.4f
            
            // Limit iterations to avoid infinite loops, create segments
            var steps = 0
            while (steps < 4 && (abs(currentX - targetX) > 50f || abs(currentY - targetY) > 50f)) {
                // Determine direction
                val dx = targetX - currentX
                val dy = targetY - currentY
                
                // Randomly choose Horizontal, Vertical, or Diagonal move
                val moveType = Random.nextInt(3)
                val stepSize = 50f + Random.nextFloat() * 100f
                
                when (moveType) {
                    0 -> { // Horizontal
                        val move = if (dx > 0) min(stepSize, dx) else max(-stepSize, dx)
                        currentX += move
                        path.lineTo(currentX, currentY)
                    }
                    1 -> { // Vertical
                        val move = if (dy > 0) min(stepSize, dy) else max(-stepSize, dy)
                        currentY += move
                        path.lineTo(currentX, currentY)
                    }
                    2 -> { // Diagonal (45 degrees)
                        val xDir = if (dx > 0) 1 else -1
                        val yDir = if (dy > 0) 1 else -1
                        val move = min(stepSize, min(abs(dx), abs(dy)))
                        currentX += move * xDir
                        currentY += move * yDir
                        path.lineTo(currentX, currentY)
                    }
                }
                steps++
            }
            
            // Add via at the end
            val viaRadius = 4f
            path.addOval(androidx.compose.ui.geometry.Rect(currentX - viaRadius, currentY - viaRadius, currentX + viaRadius, currentY + viaRadius))

            paths.add(CircuitPath(path, if (Random.nextBoolean()) Vermilion else MagmaOrange))
        }
        return paths
    }
}
