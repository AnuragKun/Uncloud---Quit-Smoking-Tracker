package com.arlabs.uncloud.di

import android.content.Context
import androidx.room.Room
import com.arlabs.uncloud.data.local.AppDatabase
import com.arlabs.uncloud.data.local.UserPreferences
import com.arlabs.uncloud.data.local.dao.AchievementDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "quit_smoking_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAchievementDao(database: AppDatabase): AchievementDao {
        return database.achievementDao()
    }

    @Provides
    fun provideBreachDao(database: AppDatabase): com.arlabs.uncloud.data.local.dao.BreachDao {
        return database.breachDao()
    }

    @Provides
    fun provideJournalDao(database: AppDatabase): com.arlabs.uncloud.data.local.dao.JournalDao {
        return database.journalDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}
