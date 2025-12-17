package com.gadgeski.abbozzo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gadgeski.abbozzo.ui.component.BruteButton
import com.gadgeski.abbozzo.ui.component.NoiseBackground
import com.gadgeski.abbozzo.ui.theme.AcidGreen
import com.gadgeski.abbozzo.ui.theme.NeonPurple

@Composable
fun CaptureScreen(
    sharedText: String?,
    onNavigateToInbox: () -> Unit,
    viewModel: CaptureViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sharedText) {
        if (sharedText != null && uiState is CaptureUiState.Idle) {
            viewModel.saveContent(sharedText)
        }
    }

    Scaffold { padding ->
        NoiseBackground(Modifier.padding(padding))
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val state = uiState) {
                is CaptureUiState.Idle -> {
                    Text("WAITING_SIGNAL...", style = MaterialTheme.typography.titleLarge)
                }
                is CaptureUiState.Saving -> {
                    Text("SAVING_DATA...", style = MaterialTheme.typography.displayMedium, color = com.gadgeski.abbozzo.ui.theme.MagmaOrange)
                }
                is CaptureUiState.Success -> {
                    Text("LOG_SECURED", style = MaterialTheme.typography.displayLarge, color = com.gadgeski.abbozzo.ui.theme.Vermilion)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Content Length: ${sharedText?.length ?: 0} chars",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    BruteButton(
                        text = "OPEN_VAULT", // Go to Inbox
                        onClick = onNavigateToInbox
                    )
                }
                is CaptureUiState.Error -> {
                    Text("SYSTEM_FAILURE", style = MaterialTheme.typography.displayMedium, color = Color.Red)
                    Text(state.message, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    BruteButton("RETRY", onClick = { /* Retry logic? */ })
                }
            }
        }
    }
}
