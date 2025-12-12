package com.arlabs.uncloud.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arlabs.uncloud.domain.repository.UserRepository
import com.arlabs.uncloud.presentation.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class MilestoneWorker
@AssistedInject
constructor(
        @Assisted appContext: Context,
        @Assisted workerParams: WorkerParameters,
        private val userRepository: UserRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_TITLE = "milestone_title"
        const val KEY_DESCRIPTION = "milestone_description"
    }

    override suspend fun doWork(): Result {
        return try {
            val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
            val description = inputData.getString(KEY_DESCRIPTION) ?: return Result.failure()

            val notificationSettings = userRepository.notificationSettings.first()

            if (notificationSettings.healthMilestonesEnabled) {
                val notificationHelper = NotificationHelper(applicationContext)
                notificationHelper.createNotificationChannel()
                notificationHelper.showMilestoneNotification(title, description)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
