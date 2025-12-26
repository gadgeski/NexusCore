package com.gadgeski.abbozzo.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.gadgeski.abbozzo.data.LogEntry
import com.gadgeski.abbozzo.ui.component.NoiseBackground
import com.gadgeski.abbozzo.ui.theme.DarkSurface
import com.gadgeski.abbozzo.ui.theme.GrayText
import com.gadgeski.abbozzo.ui.theme.MagmaOrange
import com.gadgeski.abbozzo.ui.theme.NeonCyan
import com.gadgeski.abbozzo.ui.theme.NeonPurple
import com.gadgeski.abbozzo.ui.theme.Vermilion

@Composable
fun InboxScreen(
    onNavigateToCapture: () -> Unit,
    onLogClick: (Long) -> Unit,
    viewModel: InboxViewModel = hiltViewModel()
) {
    val logs by viewModel.logs.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()



    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = DarkSurface,
                    contentColor = Color.White,
                    actionColor = Vermilion,
                    shape = CutCornerShape(topEnd = 16.dp),
                    actionContentColor = Vermilion // Explicitly set action content color
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCapture,
                containerColor = Vermilion,
                contentColor = Color.White,
                shape = CutCornerShape(20.dp),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Log",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        NoiseBackground(Modifier.padding(padding))

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            // Header with Gradient and Glow
            val titleGradient = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(
                    Vermilion,
                    MagmaOrange
                )
            )

            Text(
                text = "Abbozzo",
                maxLines = 1,
                softWrap = false,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 40.sp,
                    brush = titleGradient,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Vermilion,
                        offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                        blurRadius = 20f
                    )
                ),
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 8.dp)
            )

            // Search Bar
            val searchQuery by viewModel.searchQuery.collectAsState()
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = {
                    Text(
                        text = "QUERY_DATABASE...",
                        color = GrayText.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Vermilion
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear, // Use Clear instead of Close for semantics
                                contentDescription = "Clear",
                                tint = GrayText
                            )
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedIndicatorColor = Vermilion,
                    unfocusedIndicatorColor = Vermilion.copy(alpha = 0.5f),
                    cursorColor = Vermilion,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = CutCornerShape(8.dp)
            )

            CyberpunkTagRow(
                tags = tags,
                selectedTag = selectedTag,
                onTagClick = { viewModel.selectTag(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (logs.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Flickering Glitch Icon
                    val infiniteTransition = rememberInfiniteTransition(label = "GlitchIcon")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(100, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "Alpha"
                    )

                    Icon(
                        imageVector = Icons.Default.BrokenImage, // Represents broken data
                        contentDescription = "No Data",
                        tint = Vermilion,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(bottom = 32.dp) // Increased spacing
                            .alpha(if (System.currentTimeMillis() % 10 > 5) alpha else 0.8f) // Chaotic flicker
                    )

                    Text(
                        text = "NO DATA",
                        style = MaterialTheme.typography.displayMedium,
                        color = MagmaOrange,
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Text(
                        text = "Share text to save.",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = Color(0xFFEEEEEE),
                        modifier = Modifier.padding(bottom = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(logs) { log ->
                        LogCard(
                            log = log,
                            onClick = { onLogClick(log.id) },
                            onCopy = {
                                val formatted = "Fix this error:\n```\n${log.content}\n```"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Error Log", formatted)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "COPIED TO CLIPBOARD", Toast.LENGTH_SHORT).show()
                            },
                            onDelete = {
                                // 1行削除（インライン化）します
                                viewModel.deleteLog(log)
                                // 直接 log を使う

                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "LOG DELETED",
                                        actionLabel = "UNDO",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.restoreLog(log)
                                // ここも直接 log でOK
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CyberpunkTagRow(
    tags: List<String>,
    selectedTag: String?,
    onTagClick: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        item {
            CyberpunkChip(
                text = "ALL",
                isSelected = selectedTag == null,
                onClick = { onTagClick(null) }
            )
        }
        items(tags) { tag ->
            CyberpunkChip(
                text = tag.uppercase(),
                isSelected = selectedTag == tag,
                onClick = { onTagClick(tag) }
            )
        }
    }
}

@Composable
fun CyberpunkChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Vermilion else Color(0xFF660000)
    val backgroundColor = if (isSelected) Vermilion else Color.Transparent
    val textColor = if (isSelected) Color.White else GrayText

    // Glow effect for selected chip
    val infiniteTransition = rememberInfiniteTransition(label = "ChipGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    Box(
        modifier = Modifier
            .height(36.dp)
            .clickable(onClick = onClick)
            .background(
                color = backgroundColor,
                shape = CutCornerShape(8.dp)
            )
            .then(
                if (!isSelected) {
                    Modifier.border(BorderStroke(1.dp, borderColor), CutCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = CutCornerShape(8.dp),
                        ambientColor = Vermilion,
                        spotColor = Vermilion
                    )
                } else Modifier
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = if (isSelected) textColor.copy(alpha = glowAlpha) else textColor
        )
    }
}

@Composable
fun LogCard(log: LogEntry, onClick: () -> Unit, onCopy: () -> Unit, onDelete: () -> Unit) {
    Card(
        onClick = onClick,
        shape = CutCornerShape(topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.tag.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonPurple
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = log.formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = GrayText
                    )
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete",
                            tint = Vermilion,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = log.content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onCopy) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = NeonCyan
                    )
                }
            }
        }
    }
}