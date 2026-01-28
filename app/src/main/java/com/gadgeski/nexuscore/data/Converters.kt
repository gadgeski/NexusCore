package com.gadgeski.nexuscore.data

import androidx.room.TypeConverter

class Converters {
    // Timestamp (Long <-> Date) converters were removed because LogEntry uses Long directly.
    // "Redundant code is not allowed."

    @Suppress("unused") // Used reflectively by Room
    @TypeConverter
    fun fromNexusMode(mode: NexusMode): String {
        return mode.name
    }

    @Suppress("unused") // Used reflectively by Room
    @TypeConverter
    fun toNexusMode(value: String): NexusMode {
        return try {
            NexusMode.valueOf(value)
        } catch (e: IllegalArgumentException) {
            // Fallback for unknown values
            NexusMode.ABBOZZO
        }
    }
}