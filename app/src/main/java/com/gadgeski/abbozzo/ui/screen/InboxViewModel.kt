package com.gadgeski.abbozzo.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gadgeski.abbozzo.data.LogEntry
import com.gadgeski.abbozzo.data.LogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val repository: LogRepository
) : ViewModel() {

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag

    val tags: StateFlow<List<String>> = repository.allLogs
        .map { logs ->
            logs.flatMap { log ->
                val regex = Regex("#\\w+")
                regex.findAll(log.content).map { it.value }.toList()
            }.distinct().sorted()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val logs: StateFlow<List<LogEntry>> = combine(
        repository.allLogs,
        _selectedTag
    ) { allLogs, selectedTag ->
        if (selectedTag == null) {
            allLogs
        } else {
            allLogs.filter { it.content.contains(selectedTag) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectTag(tag: String?) {
        _selectedTag.value = tag
    }

    fun addLog(content: String) {
        viewModelScope.launch { 
            repository.addLog(content, "USER_LOG")
        }
    }

    fun deleteLog(log: LogEntry) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }
}
