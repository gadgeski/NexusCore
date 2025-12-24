package com.gadgeski.abbozzo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gadgeski.abbozzo.ui.screen.CaptureScreen
import com.gadgeski.abbozzo.ui.screen.InboxScreen
import com.gadgeski.abbozzo.ui.screen.InboxViewModel
import com.gadgeski.abbozzo.ui.theme.AbbozzoTheme
import com.gadgeski.abbozzo.ui.theme.BlackBackground
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val inboxViewModel: InboxViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Handle intent if activity is created/recreated
        handleSendText(intent)

        setContent {
            AbbozzoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BlackBackground
                ) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the intent property
        handleSendText(intent)
    }

    private fun handleSendText(intent: Intent) {
        if (Intent.ACTION_SEND == intent.action && "text/plain" == intent.type) {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedText ->
                if (sharedText.isNotBlank()) {
                    inboxViewModel.addLog(sharedText)
                    Toast.makeText(this, "Saved via Abbozzo", Toast.LENGTH_SHORT).show()
                    // Prevent duplicate saving on configuration changes
                    intent.removeExtra(Intent.EXTRA_TEXT)
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 変更前: startDestination = "capture"
    // 変更後: "inbox" に変更します
    NavHost(navController = navController, startDestination = "inbox") {
        composable("inbox") {
            InboxScreen(
                onNavigateToCapture = { navController.navigate("capture") }
            )
        }
        composable("capture") {
            CaptureScreen(
                onNavigateToInbox = {
                    navController.navigate("inbox") {
                        popUpTo("inbox") { inclusive = true } // Clear stack to avoid loop
                    }
                }
            )
        }
    }
}