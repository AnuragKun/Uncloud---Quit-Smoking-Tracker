package com.arlabs.uncloud.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class QuitSmokingApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        enqueueWidgetUpdates()
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
        
        enqueueDailyNotifications()
    }

    private fun enqueueDailyNotifications() {
        // Daily Motivation (Every 24 hours)
        val motivationRequest = androidx.work.PeriodicWorkRequestBuilder<com.arlabs.uncloud.worker.DailyMotivationWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        ).build()

        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyMotivationWork",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            motivationRequest
        )
    }
}
