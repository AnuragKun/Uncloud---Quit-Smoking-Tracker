package com.arlabs.uncloud.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.arlabs.uncloud.domain.repository.UserRepository

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    val userRepository: UserRepository
}