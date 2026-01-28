package com.gadgeski.nexuscore.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// NexusCore: Abbozzo Mode (Red/Black Industrial)
private val AbbozzoColorScheme = darkColorScheme(
    primary = Vermilion,
    // 主役：朱色
    onPrimary = BlackBackground,
    secondary = NeonPurple,
    // アクセント：ネオンパープル
    onSecondary = WhiteHighContrast,
    tertiary = AcidGreen,
    // 刺し色
    onTertiary = BlackBackground,
    background = BlackBackground,
    // 完全な漆黒
    onBackground = WhiteHighContrast,
    surface = DarkRedSurface,
    // マグマのような暗い赤黒
    onSurface = WhiteHighContrast,
    error = MagmaOrange
)

// Abbozzoの特徴である「角張った形状」
val AbbozzoShapes = Shapes(
    small = CutCornerShape(4.dp),
    medium = CutCornerShape(8.dp),
    large = CutCornerShape(16.dp)
)

@Composable
fun NexusCoreTheme(
    // unused parameter "darkTheme" を削除。NexusCoreは常にDarkです。
    content: @Composable () -> Unit
) {
    val colorScheme = AbbozzoColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 警告原因の window.statusBarColor = ... を削除
            // Edge-to-Edgeにより背景色が透けるため、アイコンの色（白）だけを制御します

            val insetsController = WindowCompat.getInsetsController(window, view)
            // 背景が黒なので、ステータスバーの文字・アイコンは「白（Lightではない）」にする
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AbbozzoShapes,
        content = content
    )
}