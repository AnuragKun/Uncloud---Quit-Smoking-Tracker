package com.arlabs.uncloud.domain.manager

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.arlabs.uncloud.domain.repository.HealthRepository
import com.arlabs.uncloud.worker.MilestoneWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MilestoneScheduler
@Inject
constructor(
        @ApplicationContext private val context: Context,
        private val healthRepository: HealthRepository
) {

    fun scheduleMilestones(quitTimestamp: Long) {
        val workManager = WorkManager.getInstance(context)

        // Cancel all previous milestone work to avoid duplicates if reset
        workManager.cancelAllWorkByTag("milestone_notification")

        val milestones = healthRepository.getMilestones()
        val now = Instant.now().toEpochMilli()

        milestones.forEach { milestone ->
            val milestoneTime = quitTimestamp + (milestone.durationSeconds * 1000)

            // Only schedule if the milestone is in the future
            if (milestoneTime > now) {
                val delay = milestoneTime - now

                val data =
                        Data.Builder()
                                .putString(MilestoneWorker.KEY_TITLE, milestone.title)
                                .putString(MilestoneWorker.KEY_DESCRIPTION, milestone.description)
                                .build()

                val workRequest =
                        OneTimeWorkRequestBuilder<MilestoneWorker>()
                                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                .setInputData(data)
                                .addTag("milestone_notification")
                                .build()

                // We use append here because we are scheduling multiple unique milestones
                // but we want unique NAMES for each if we used enqueueUniqueWork,
                // however enqueueUniqueWork is per-request-name.
                // Since we have many milestones, using a tag + simple enqueue is easier,
                // relying on cancelAllWorkByTag for cleanup.

                workManager.enqueue(workRequest)
            }
        }
    }

    fun cancelAll() {
        WorkManager.getInstance(context).cancelAllWorkByTag("milestone_notification")
    }
}
