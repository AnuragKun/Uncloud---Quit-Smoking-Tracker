package com.arlabs.uncloud.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arlabs.uncloud.domain.manager.QuoteManager
import com.arlabs.uncloud.domain.repository.UserRepository
import com.arlabs.uncloud.presentation.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.first

@HiltWorker
class DailyMotivationWorker
@AssistedInject
constructor(
        @Assisted appContext: Context,
        @Assisted workerParams: WorkerParameters,
        private val userRepository: UserRepository,
        private val quoteManager: QuoteManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val userConfig = userRepository.userConfig.first()

            // Only show notification if user has configured quit date
            if (userConfig != null) {
                val notificationSettings = userRepository.notificationSettings.first()
                if (!notificationSettings.dailyMotivationEnabled) {
                    return Result.success()
                }

                val quitDate = Instant.ofEpochMilli(userConfig.quitTimestamp)
                val now = Instant.now()

                // Calculate streak in days
                val daysSmokeFree = ChronoUnit.DAYS.between(quitDate, now)

                // Get Daily Quote
                val quote = quoteManager.getDailyQuote()

                val title =
                        if (daysSmokeFree > 0) {
                            "Day $daysSmokeFree Smoke Free! \uD83D\uDD25"
                        } else {
                            "Your Journey Begins! \uD83D\uDD25"
                        }

                val notificationHelper = NotificationHelper(applicationContext)
                // Ensure channel exists
                notificationHelper.createNotificationChannel()

                // Check for Rank Up!
                val rank = com.arlabs.uncloud.domain.model.rankSystem.find { it.daysRequired.toLong() == daysSmokeFree }
                if (rank != null) {
                    notificationHelper.showRankNotification(rank.title, rank.description)
                }

                // Show Daily Motivation (Standard)
                notificationHelper.showNotification(
                        title = title,
                        content = "\"${quote.text}\" - ${quote.author}"
                )
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
