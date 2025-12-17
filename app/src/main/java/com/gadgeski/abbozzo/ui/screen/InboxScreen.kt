package com.gadgeski.abbozzo.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gadgeski.abbozzo.data.LogEntry
import com.gadgeski.abbozzo.ui.component.NoiseBackground
import com.gadgeski.abbozzo.ui.theme.DarkSurface
import com.gadgeski.abbozzo.ui.theme.GrayText
import com.gadgeski.abbozzo.ui.theme.NeonCyan
import com.gadgeski.abbozzo.ui.theme.NeonPurple

@Composable
fun InboxScreen(
    viewModel: InboxViewModel = hiltViewModel()
) {
    val logs by viewModel.logs.collectAsState()
    val context = LocalContext.current

    Scaffold { padding ->
        NoiseBackground(Modifier.padding(padding))

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            
            // Header
            // Header with Gradient and Glow
            val titleGradient = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(
                    com.gadgeski.abbozzo.ui.theme.Vermilion,
                    com.gadgeski.abbozzo.ui.theme.MagmaOrange
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
                        color = com.gadgeski.abbozzo.ui.theme.Vermilion,
                        offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                        blurRadius = 20f
                    )
                ),
                modifier = Modifier.padding(24.dp)
            )

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
                        tint = com.gadgeski.abbozzo.ui.theme.Vermilion,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(bottom = 32.dp) // Increased spacing
                            .alpha(if (System.currentTimeMillis() % 10 > 5) alpha else 0.8f) // Chaotic flicker
                    )

                    Text(
                        text = "NO DATA",
                        style = MaterialTheme.typography.displayMedium, // Large and Bold (uses BbhBartle by default for DisplayMedium now)
                        color = com.gadgeski.abbozzo.ui.theme.MagmaOrange,
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

                    com.gadgeski.abbozzo.ui.component.BruteButton(
                        text = "DEBUG ADD",
                        onClick = { viewModel.addDebugLog() },
                        modifier = Modifier.padding(horizontal = 48.dp)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(logs) { log ->
                        LogCard(log = log, onCopy = {
                            val formatted = "Fix this error:\n```\n${log.content}\n```"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Error Log", formatted)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "COPIED TO CLIPBOARD", Toast.LENGTH_SHORT).show()
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun LogCard(log: LogEntry, onCopy: () -> Unit) {
    Card(
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
                Text(
                    text = log.formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = GrayText
                )
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
