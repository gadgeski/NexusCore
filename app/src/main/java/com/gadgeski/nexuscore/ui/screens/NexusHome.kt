package com.gadgeski.nexuscore.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gadgeski.nexuscore.data.LogEntry
import com.gadgeski.nexuscore.data.NexusMode
import com.gadgeski.nexuscore.ui.components.AbbozzoInput
import com.gadgeski.nexuscore.ui.components.NoiseBackground
import com.gadgeski.nexuscore.ui.theme.Vermilion
import com.gadgeski.nexuscore.ui.theme.NeonPurple
import com.gadgeski.nexuscore.ui.theme.NeonCyan
import com.gadgeski.nexuscore.ui.theme.DailyMustard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexusHome(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val logs by viewModel.logList.collectAsState()
    val currentMode by viewModel.currentMode.collectAsState()

    // BottomSheet State
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // --- Dynamic Theme Logic ---
    // モードに応じてメインカラーとアクセントカラーを動的に決定
    val themePrimary = when(currentMode) {
        NexusMode.ABBOZZO -> Vermilion
        NexusMode.DAILY_SYNC -> DailyMustard
        NexusMode.BUG_MEMO -> NeonCyan
    }

    val themeSecondary = when(currentMode) {
        NexusMode.ABBOZZO -> NeonPurple
        NexusMode.DAILY_SYNC -> Color(0xFFE6DCCD)
        NexusMode.BUG_MEMO -> Color(0xFF64FFDA)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // [Layer 1] Noise Background (Dynamic)
        NoiseBackground(
            modifier = Modifier.fillMaxSize(),
            scanlineColor = themePrimary,
            particleColor = themeSecondary
        )

        // [Layer 2] Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Header (Clickable Mode Switcher)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showBottomSheet = true }
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "NEXUS_CORE",
                    style = MaterialTheme.typography.displayMedium,
                    color = themePrimary,
                    lineHeight = 50.sp
                )
                Text(
                    text = "// SYSTEM_MODE :: ${currentMode.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = themePrimary.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
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
                        SwipeToDeleteContainer(
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

        // [Layer 3] Input Field (Dynamic)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .systemBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            AbbozzoInput(
                onSend = { message ->
                    viewModel.onInputReceived(message)
                },
                primaryColor = themePrimary,
                secondaryColor = themeSecondary
            )
        }

        // [Layer 4] Mode Selection Bottom Sheet
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "SELECT PROTOCOL",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp),
                        color = themePrimary
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    NexusMode.entries.forEach { mode ->
                        // 各選択肢のテーマ色を定義
                        val modeColor = when(mode) {
                            NexusMode.ABBOZZO -> Vermilion
                            NexusMode.DAILY_SYNC -> DailyMustard
                            NexusMode.BUG_MEMO -> NeonCyan
                        }

                        ListItem(
                            headlineContent = {
                                Text(
                                    text = mode.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            supportingContent = {
                                val description = when(mode) {
                                    NexusMode.ABBOZZO -> "Cyberpunk / Raw Input / Glitch"
                                    NexusMode.DAILY_SYNC -> "Lifestyle / Journal / Calm"
                                    NexusMode.BUG_MEMO -> "Technical / Hacker / Trace"
                                }
                                Text(text = description, style = MaterialTheme.typography.labelSmall)
                            },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(modeColor, RoundedCornerShape(2.dp))
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                                headlineColor = if (currentMode == mode) modeColor else Color.White,
                                supportingColor = Color.Gray
                            ),
                            modifier = Modifier.clickable {
                                viewModel.onModeSelected(mode)
                                showBottomSheet = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// Helper for Box size in ListItem
@Composable
private fun Modifier.size(size: androidx.compose.ui.unit.Dp): Modifier {
    return this
        .width(size)
        .height(size)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

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
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF0033)
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
        enableDismissFromStartToEnd = false,
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
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