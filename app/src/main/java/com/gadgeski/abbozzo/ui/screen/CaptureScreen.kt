package com.gadgeski.abbozzo.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gadgeski.abbozzo.ui.component.BruteButton
import com.gadgeski.abbozzo.ui.component.NoiseBackground
import com.gadgeski.abbozzo.ui.theme.GrayText
import com.gadgeski.abbozzo.ui.theme.Vermilion

@Composable
fun CaptureScreen(
    onNavigateToInbox: () -> Unit,
    viewModel: CaptureViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var text by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is CaptureUiState.Success) {
            viewModel.resetState() // Reset for next time
            onNavigateToInbox()
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
            // verticalArrangement = Arrangement.Center // Removed to align top-heavy for editor
        ) {
            Text(
                text = "MANUAL_OVERRIDE",
                style = MaterialTheme.typography.displaySmall,
                color = Vermilion,
                modifier = Modifier.padding(bottom = 16.dp).align(Alignment.Start)
            )

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = {
                    Text(
                        "ENTER_SIGNAL...",
                        color = GrayText.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Fill available space
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Vermilion,
                    unfocusedIndicatorColor = Vermilion.copy(alpha = 0.5f),
                    cursorColor = Vermilion
                ),
                shape = CutCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is CaptureUiState.Idle, is CaptureUiState.Success -> {
                   BruteButton(
                       text = "EXECUTE",
                       onClick = { 
                           if (text.isNotBlank()) {
                               viewModel.saveContent(text) 
                           }
                       },
                       enabled = text.isNotBlank()
                   )
                }
                is CaptureUiState.Saving -> {
                    Text("SAVING_DATA...", style = MaterialTheme.typography.displayMedium, color = com.gadgeski.abbozzo.ui.theme.MagmaOrange)
                }
                is CaptureUiState.Error -> {
                    Text("SYSTEM_FAILURE", style = MaterialTheme.typography.displayMedium, color = Color.Red)
                    Text(state.message, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    BruteButton("RETRY", onClick = { 
                          if (text.isNotBlank()) viewModel.saveContent(text) 
                    })
                }
            }
        }
    }
}
