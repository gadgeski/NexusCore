package com.gadgeski.nexuscore.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gadgeski.nexuscore.data.LogEntry
import com.gadgeski.nexuscore.data.LogRepository
import com.gadgeski.nexuscore.data.NexusMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: LogRepository
) : ViewModel() {

    // UI State: データベースの変更を監視
    val logList: StateFlow<List<LogEntry>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Mode State: 現在選択されている人格（デフォルトはAbbozzo）
    private val _currentMode = MutableStateFlow(NexusMode.ABBOZZO)
    val currentMode: StateFlow<NexusMode> = _currentMode.asStateFlow()

    /**
     * モード切り替え
     */
    fun onModeSelected(mode: NexusMode) {
        _currentMode.value = mode
    }

    /**
     * 入力を処理
     * 現在選択されているモードをタグとして付与して保存
     */
    fun onInputReceived(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            // 現在のモードを使用してログを保存
            repository.addLog(content, _currentMode.value)
        }
    }

    /**
     * ログの削除
     */
    fun onDeleteRequested(log: LogEntry) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }
}