package com.gadgeski.nexuscore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.gadgeski.nexuscore.ui.screens.NexusHome
import com.gadgeski.nexuscore.ui.theme.NexusCoreTheme
import dagger.hilt.android.AndroidEntryPoint

// 重要: Hiltを使用するため、必ず @AndroidEntryPoint を付与します
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ステータスバー透過設定（以前のコードから継承）
        enableEdgeToEdge()

        setContent {
            NexusCoreTheme {
                // アプリ全体の背景コンテナ
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ここでNexusHomeを呼び出し、アプリのメイン機能を起動します
                    NexusHome()
                }
            }
        }
    }
}