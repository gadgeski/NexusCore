package com.gadgeski.nexuscore.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    // タイムスタンプの降順（新しい順）で全件取得
    // Flowを返すことで、DB更新時に自動的に新しいリストが流れてくる
    @Query("SELECT * FROM logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<LogEntry>>

    // 書き込みはサスペンド関数（非同期）
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogEntry)

    @Delete
    suspend fun deleteLog(log: LogEntry)

    // 特定のモードだけ抽出するクエリ（将来用）
    @Query("SELECT * FROM logs WHERE mode = :targetMode ORDER BY timestamp DESC")
    fun getLogsByMode(targetMode: NexusMode): Flow<List<LogEntry>>
}