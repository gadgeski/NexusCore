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
import com.gadgeski.nexuscore.ui.components.NoiseBackground

@Composable
fun NexusHome(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val logs by viewModel.logList.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            // 重要: ここ以外で .background を使わないこと！
            .background(MaterialTheme.colorScheme.background)
    ) {
        // [Layer 1] Noise Background
        // 一番最初に書くことで、最背面に描画されます
        NoiseBackground(
            modifier = Modifier.fillMaxSize()
        )

        // [Layer 2] Content (Text, List)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
            // ここに .background があるとノイズが消えます。絶対に書かないこと。
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "NEXUS_CORE",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 50.sp
                )
                Text(
                    text = "// STORAGE_SYSTEM :: ONLINE",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            }

            // Log Stream
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

            Spacer(modifier = Modifier.height(8.dp))
        }

        // [Layer 3] Input Field (Floating on bottom)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .systemBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            AbbozzoInput(
                onSend = { message ->
                    viewModel.onInputReceived(message)
                }
            )
        }
    }
}

@Composable
fun LogItem(log: LogEntry) {
    val accentColor = when (log.mode) {
        NexusMode.ABBOZZO -> MaterialTheme.colorScheme.primary
        NexusMode.DAILY_SYNC -> Color(0xFFD4AF37)
        NexusMode.BUG_MEMO -> Color(0xFF64FFDA)
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .background(accentColor)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "> ${log.content}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${log.formattedDate} :: ${log.mode.name}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
    }
}