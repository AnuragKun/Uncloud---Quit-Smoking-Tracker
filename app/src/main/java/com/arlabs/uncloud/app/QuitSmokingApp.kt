package com.arlabs.uncloud.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class QuitSmokingApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var dailyMotivationScheduler: com.arlabs.uncloud.domain.manager.DailyMotivationScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        enqueueWidgetUpdates()
        
        // Schedule Daily Motivation (Default 9:00 AM, KEEP policy to respect user changes)
        dailyMotivationScheduler.schedule(9, 0, androidx.work.ExistingPeriodicWorkPolicy.KEEP)
    }

    private fun enqueueWidgetUpdates() {
        val request = androidx.work.PeriodicWorkRequestBuilder<com.arlabs.uncloud.presentation.workers.WidgetUpdateWorker>(
            15, java.util.concurrent.TimeUnit.MINUTES // Update every 15 mins for better responsiveness? Widget updates are cheap.
        ).build()

        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "WidgetUpdateWork",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
    
    // Removed raw enqueueDailyNotifications as it's handled by scheduler now
}
