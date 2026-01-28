package com.gadgeski.nexuscore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gadgeski.nexuscore.data.LogEntry
import com.gadgeski.nexuscore.data.NexusMode
import com.gadgeski.nexuscore.ui.components.AbbozzoInput

@Composable
fun NexusHome(
    // HiltからViewModelを注入。ここでHomeViewModelが初めてインスタンス化されます。
    viewModel: HomeViewModel = hiltViewModel()
) {
    // データベースの変更を監視 (StateFlow -> Compose State)
    // これにより HomeViewModel.logList の参照警告が解消されます
    val logs by viewModel.logList.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding() // Edge-to-Edge対応
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header: アプリの状態を示すヘッダー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "NEXUS_CORE // STORAGE",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            }

            // Log Stream: データベースの中身を表示
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = false
            ) {
                if (logs.isEmpty()) {
                    item {
                        Text(
                            text = "> DATABASE EMPTY\n> WAITING FOR INPUT...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.padding(top = 32.dp, start = 8.dp)
                        )
                    }
                } else {
                    items(logs, key = { it.id }) { log ->
                        LogItem(log = log)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // Input Fieldとの余白
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Input Field: 画面下部に固定
        AbbozzoInput(
            onSend = { message ->
                // 入力イベントをViewModelへ送信
                // これにより HomeViewModel.onInputReceived の参照警告が解消されます
                viewModel.onInputReceived(message)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun LogItem(log: LogEntry) {
    // モードに応じたアクセントカラーを決定
    val accentColor = when (log.mode) {
        NexusMode.ABBOZZO -> MaterialTheme.colorScheme.primary // Vermilion
        NexusMode.DAILY_SYNC -> Color(0xFFD4AF37) // Mustard
        NexusMode.BUG_MEMO -> Color(0xFF64FFDA)   // Cyan
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Mode Indicator Line (左側のカラーバー)
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp) // 高さは最低限を確保
                .background(accentColor)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            // ログ本文
            Text(
                text = "> ${log.content}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            // メタデータ（日付とモード）
            // LogEntry.formattedDate の参照警告がここで解消されます
            Text(
                text = "${log.formattedDate} :: ${log.mode.name}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
    }
}