package com.gadgeski.nexuscore.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogRepository @Inject constructor(
    private val logDao: LogDao
) {
    // UIが監視するデータストリーム
    val allLogs: Flow<List<LogEntry>> = logDao.getAllLogs()

    // ログを追加する
    suspend fun addLog(content: String, mode: NexusMode = NexusMode.ABBOZZO) {
        val entry = LogEntry(
            content = content,
            mode = mode
            // timestamp, id は自動生成
        )
        logDao.insertLog(entry)
    }

    // ログを削除する
    suspend fun deleteLog(log: LogEntry) {
        logDao.deleteLog(log)
    }
}