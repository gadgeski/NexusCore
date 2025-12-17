package com.gadgeski.abbozzo.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gadgeski.abbozzo.data.LogEntry
import com.gadgeski.abbozzo.data.LogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val repository: LogRepository
) : ViewModel() {

    val logs: StateFlow<List<LogEntry>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addDebugLog() {
        val dummy = "Exception in thread \"main\" java.lang.NullPointerException: The \"life\" object is null.\n\tat com.universe.Existence.meaning(Existence.java:42)\n\tat com.humanity.Reality.check(Reality.java:101)"
        viewModelScope.launch { 
            repository.addLog(dummy, "DEBUG_SYS")
        }
    }
}
