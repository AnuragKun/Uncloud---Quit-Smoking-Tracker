package com.arlabs.uncloud.di

import com.arlabs.uncloud.domain.manager.WidgetRefresher
import com.arlabs.uncloud.presentation.widgets.manager.GlanceWidgetRefresher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WidgetModule {

    @Binds
    @Singleton
    abstract fun bindWidgetRefresher(
        glanceWidgetRefresher: GlanceWidgetRefresher
    ): WidgetRefresher
}
