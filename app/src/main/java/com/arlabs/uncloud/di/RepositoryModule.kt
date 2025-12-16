package com.arlabs.uncloud.di

import com.arlabs.uncloud.data.repository.AchievementRepositoryImpl
import com.arlabs.uncloud.data.repository.HealthRepositoryImpl
import com.arlabs.uncloud.data.repository.UserRepositoryImpl
import com.arlabs.uncloud.data.repository.JournalRepositoryImpl
import com.arlabs.uncloud.domain.repository.AchievementRepository
import com.arlabs.uncloud.domain.repository.HealthRepository
import com.arlabs.uncloud.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
    @Binds
    abstract fun bindAchievementRepository(
            achievementRepositoryImpl: AchievementRepositoryImpl
    ): AchievementRepository

    @Binds
    abstract fun bindHealthRepository(healthRepositoryImpl: HealthRepositoryImpl): HealthRepository

    @Binds
    abstract fun bindJournalRepository(journalRepositoryImpl: JournalRepositoryImpl): com.arlabs.uncloud.domain.repository.JournalRepository
}
