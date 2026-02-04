package com.gadgeski.nexuscore.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Version updated to 2 (Schema changed)
@Database(entities = [LogEntry::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
}