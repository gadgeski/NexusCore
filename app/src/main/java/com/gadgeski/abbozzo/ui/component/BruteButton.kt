package com.gadgeski.abbozzo.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.gadgeski.abbozzo.ui.theme.DarkRedBlack
import com.gadgeski.abbozzo.ui.theme.DarkRedSurface
import com.gadgeski.abbozzo.ui.theme.Vermilion
import com.gadgeski.abbozzo.ui.theme.WhiteHighContrast

@Composable
fun BruteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent, // Not used directly logic-wise for gradient, but kept for compatibility if passed
    contentColor: Color = WhiteHighContrast
) {
    val gradientBrush = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(
            com.gadgeski.abbozzo.ui.theme.DarkRedSurface,
            com.gadgeski.abbozzo.ui.theme.DarkRedBlack
        )
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = CutCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, Vermilion), // Bright border
        elevation = ButtonDefaults.buttonElevation(0.dp) // Flat industrial look
    ) {
        // Gradient background box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text.uppercase(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
