package com.gadgeski.nexuscore.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.gadgeski.nexuscore.ui.theme.Vermilion
import java.util.Locale

private fun speechErrorToMessage(error: Int): String = when (error) {
    SpeechRecognizer.ERROR_AUDIO -> "音声入力エラー（マイク）"
    SpeechRecognizer.ERROR_CLIENT -> "音声入力エラー（クライアント）"
    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "マイク権限が不足しています"
    SpeechRecognizer.ERROR_NETWORK -> "ネットワークエラー"
    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "ネットワークがタイムアウトしました"
    SpeechRecognizer.ERROR_NO_MATCH -> "音声を認識できませんでした"
    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "音声認識サービスが使用中です"
    SpeechRecognizer.ERROR_SERVER -> "音声認識サーバーエラー"
    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "音声入力がタイムアウトしました"
    else -> "不明なエラー: $error"
}

@Composable
fun AbbozzoInput(
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color = Vermilion,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary
) {
    var text by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // SpeechRecognizer初期化（Context変化時に作り直す）
    val speechRecognizer = remember(context) {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    // 音声認識インテント
    val speechIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    // 重複処理を減らすための開始関数
    fun startListeningSafely() {
        try {
            speechRecognizer.startListening(speechIntent)
            isListening = true
        } catch (_: Exception) {
            isListening = false
            Toast.makeText(context, "音声入力を開始できませんでした", Toast.LENGTH_SHORT).show()
        }
    }

    val listener = remember(context) {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit

            override fun onEndOfSpeech() {
                isListening = false
            }

            override fun onError(error: Int) {
                isListening = false
                // ここで errorMessage を実使用 → 警告解消
                val errorMessage = speechErrorToMessage(error)
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    text = if (text.isBlank()) recognizedText else "$text $recognizedText"
                }
                isListening = false
            }

            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        }
    }

    // ライフサイクルに合わせてリスナー登録/破棄
    DisposableEffect(speechRecognizer, listener) {
        speechRecognizer.setRecognitionListener(listener)
        onDispose {
            runCatching { speechRecognizer.stopListening() }
            speechRecognizer.destroy()
        }
    }

    // 権限リクエスト
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListeningSafely()
        } else {
            Toast.makeText(context, "マイク権限がないため音声入力できません", Toast.LENGTH_SHORT).show()
        }
    }

    // マイク点滅
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val micAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 0.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_alpha"
    )

    // マイク色変化
    val micTint by animateColorAsState(
        targetValue = if (isListening) Color.Red else secondaryColor,
        label = "mic_tint"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = primaryColor,
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
            IconButton(
                onClick = {
                    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                        Toast.makeText(context, "この端末では音声認識を利用できません", Toast.LENGTH_SHORT)
                            .show()
                        return@IconButton
                    }

                    if (isListening) {
                        speechRecognizer.stopListening()
                        isListening = false
                    } else {
                        if (
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            startListeningSafely()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "Voice Input",
                    tint = micTint,
                    modifier = Modifier.alpha(micAlpha)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

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
                cursorBrush = SolidColor(primaryColor),
                singleLine = false,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (text.isNotBlank()) {
                            onSend(text)
                            text = ""
                        }
                    }
                ),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = if (isListening) "LISTENING..." else "INPUT_STREAM...",
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

            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                    }
                },
                modifier = Modifier
                    .background(primaryColor, CutCornerShape(4.dp))
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
