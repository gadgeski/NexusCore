package com.gadgeski.nexuscore.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gadgeski.nexuscore.R

// Custom Font: Bbh Bartle (Extremely Bold / Impactful)
// ユーザー指定の極太フォント
val BbhBartle = FontFamily(
    Font(R.font.bbh_bartle_regular, FontWeight.Normal),
    Font(R.font.bbh_bartle_regular, FontWeight.Bold)
    // Reuse regular for bold if no bold file, or let system synthesize
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = BbhBartle,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
        color = WhiteHighContrast
    ),
    displayMedium = TextStyle(
        fontFamily = BbhBartle,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
        color = WhiteHighContrast
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        color = WhiteHighContrast
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = WhiteHighContrast
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = GrayText
    )
)