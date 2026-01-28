package com.gadgeski.nexuscore.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * NexusCoreの動作モード定義
 * 単なるメモの分類ではなく、UI/UXの世界観そのものを決定するコンテキスト。
 */
enum class NexusMode {
    ABBOZZO,    // Default: Cyberpunk / Raw Input / Glitch
    DAILY_SYNC, // Lifestyle / Journal / Serif Font
    BUG_MEMO    // Technical / Hacker / Monospace
}

@Entity(tableName = "logs")
data class LogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String = "", // 将来的にはカンマ区切りで複数タグ対応予定
    val mode: NexusMode = NexusMode.ABBOZZO // デフォルトは攻撃的なAbbozzoモード
) {
    val formattedDate: String
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}