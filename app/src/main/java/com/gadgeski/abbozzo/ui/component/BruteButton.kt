package com.gadgeski.abbozzo.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gadgeski.abbozzo.ui.theme.DarkRedBlack
import com.gadgeski.abbozzo.ui.theme.DarkRedSurface
import com.gadgeski.abbozzo.ui.theme.Vermilion
import com.gadgeski.abbozzo.ui.theme.WhiteHighContrast

@Composable
fun BruteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    // ★追加: これでCaptureScreenのエラーが解消
    contentColor: Color = WhiteHighContrast
) {
    // 有効/無効で色を切り替える（サイバーパンクな演出）
    val borderStroke = if (enabled) {
        BorderStroke(1.dp, Vermilion)
    // ON: 鮮やかな朱色
    } else {
        BorderStroke(1.dp, Color.DarkGray)
    // OFF: 鉄くずのようなグレー
    }

    val backgroundBrush = if (enabled) {
        Brush.verticalGradient(
            colors = listOf(DarkRedSurface, DarkRedBlack)
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(Color.Black, Color.DarkGray)
        // OFF: 電源オフ状態
        )
    }

    val finalContentColor = if (enabled) contentColor else Color.Gray

    Button(
        onClick = onClick,
        enabled = enabled,
        // ★ここ重要: ボタン自体の機能を無効化
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = CutCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = finalContentColor,
            disabledContainerColor = Color.Transparent,
            // 無効時も背景は自作Boxに任せる
            disabledContentColor = Color.Gray
        ),
        border = borderStroke,
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        // Gradient background box
        // Buttonの内部コンテンツとして背景を描画することで、ボタン形状（CutCorner）に合わせてクリップされる
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush),
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