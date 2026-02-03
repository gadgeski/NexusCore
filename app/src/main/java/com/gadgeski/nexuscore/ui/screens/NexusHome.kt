package com.gadgeski.nexuscore.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
                    .fillMaxWidth(),
                reverseLayout = false
            ) {
                if (logs.isEmpty()) {
                    item {
                        Text(
                            text = "> DATABASE EMPTY\n> WAITING FOR INPUT...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.padding(top = 32.dp, start = 24.dp)
                        )
                    }
                } else {
                    items(logs, key = { it.id }) { log ->
                        // Swipe to Dismiss Implementation
                        SwipeToDeleteContainer(
                            // itemパラメータは不要になったため削除
                            onDelete = { viewModel.onDeleteRequested(log) }
                        ) {
                            LogItem(log = log)
                        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(
    // item: LogEntry, // Warning Resolved: Unused parameter removed
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    // Warning Resolved: confirmValueChange is deprecated.
    // Use simple state initialization and observe changes via LaunchedEffect instead.
    val dismissState = rememberSwipeToDismissBoxState()

    // 状態監視: スワイプが完了(EndToStart)した瞬間に削除を実行
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF0033) // Vivid Red for Deletion
                    else -> Color.Transparent
                }, label = "bgColor"
            )

            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 0.8f,
                label = "iconScale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.scale(scale)
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false, // Disable swipe right
        content = { content() }
    )
}

@Composable
fun LogItem(log: LogEntry) {
    val accentColor = when (log.mode) {
        NexusMode.ABBOZZO -> MaterialTheme.colorScheme.primary
        NexusMode.DAILY_SYNC -> Color(0xFFD4AF37)
        NexusMode.BUG_MEMO -> Color(0xFF64FFDA)
    }

    // Swipe時に背景色が透けないよう、Item自体にも背景色(Surface)を持たせるか検討が必要だが
    // Abbozzoは黒背景に直接描画するスタイルなので、Rowにbackgroundを付けないのが正解。
    // ただし、SwipeToDismissの仕様上、コンテンツが透明だと裏の「赤」が透けて見えるため、
    // コンテンツには「黒背景」を明示的に指定して、通常時は裏が見えないようにする。
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black) // 【重要】裏側の赤い色を隠すため
            .padding(horizontal = 16.dp) // リスト全体のパディングをここに移動
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