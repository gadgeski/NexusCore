package com.gadgeski.nexuscore.di

import android.content.Context
import androidx.room.Room
import com.gadgeski.nexuscore.data.AppDatabase
import com.gadgeski.nexuscore.data.LogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "abbozzo_db" // 既存のDB名を維持
        )
            // 開発フェーズ用: スキーマ変更時（Ver 1 -> 2等）にクラッシュせず、DBを再作成する安全装置
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideLogDao(database: AppDatabase): LogDao {
        return database.logDao()
    }
}