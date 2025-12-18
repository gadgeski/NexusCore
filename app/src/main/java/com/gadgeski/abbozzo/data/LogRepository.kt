package com.gadgeski.abbozzo.data

import javax.inject.Inject

class LogRepository @Inject constructor(private val logDao: LogDao) {
    val allLogs = logDao.getAllLogs()

    suspend fun addLog(content: String, tag: String = "General") {
        logDao.insertLog(LogEntry(content = content, tag = tag))
    }

    suspend fun deleteLog(log: LogEntry) {
        logDao.deleteLog(log)
    }
}
