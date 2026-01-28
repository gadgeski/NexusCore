package com.gadgeski.nexuscore.data

import javax.inject.Inject

class LogRepository @Inject constructor(private val logDao: LogDao) {
    val allLogs = logDao.getAllLogs()

    suspend fun getLogById(id: Long): LogEntry? = logDao.getLogById(id)

    suspend fun addLog(content: String, tag: String = "General", id: Long = 0) {
        logDao.insertLog(LogEntry(id = id, content = content, tag = tag))
    }

    suspend fun deleteLog(log: LogEntry) {
        logDao.deleteLog(log)
    }
}
