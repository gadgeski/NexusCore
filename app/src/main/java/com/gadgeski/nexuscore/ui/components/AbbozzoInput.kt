package com.gadgeski.nexuscore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gadgeski.nexuscore.ui.theme.Vermilion

@Composable
fun AbbozzoInput(
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Vermilion, // 【追加】デフォルト色
    secondaryColor: Color = MaterialTheme.colorScheme.secondary // 【追加】
) {
    var text by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = primaryColor, // 【変更】動的カラー適用
                shape = CutCornerShape(topStart = 16.dp)
            ),
        color = MaterialTheme.colorScheme.surface,
        shape = CutCornerShape(topStart = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Voice Input Trigger
            IconButton(
                onClick = { /* TODO: Implement Voice to Glitch */ },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = secondaryColor // 【変更】動的カラー適用
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Command Line Input
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                cursorBrush = SolidColor(primaryColor), // 【変更】カーソル色も変更
                singleLine = false,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onDone = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                    }
                }),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = "INPUT_STREAM...",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontSize = 16.sp
                            )
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Execute Button
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                    }
                },
                modifier = Modifier
                    .background(primaryColor, CutCornerShape(4.dp)) // 【変更】ボタン背景も変更
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Execute",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}