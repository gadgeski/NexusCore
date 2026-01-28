package com.gadgeski.nexuscore.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gadgeski.nexuscore.data.LogEntry
import com.gadgeski.nexuscore.data.LogRepository
import com.gadgeski.nexuscore.data.NexusMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: LogRepository
) : ViewModel() {

    // UI State: データベースの変更を監視し、常に最新のリストを保持する
    // WhileSubscribed(5000)により、画面回転時などの一時的な切断で購読を止めない
    val logList: StateFlow<List<LogEntry>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * AbbozzoInputからの入力を処理
     * デフォルトではAbbozzoモード（攻撃的メモ）として保存
     */
    fun onInputReceived(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            // 将来的にAIによる自動タグ付けやモード判定ロジックをここに挟む
            repository.addLog(content, NexusMode.ABBOZZO)
        }
    }

    /**
     * ログの削除（スワイプ削除などを想定）
     */
    fun onDeleteRequested(log: LogEntry) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }
}