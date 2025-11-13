package com.example.taskmanagerapp.ui.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkScheduler {

    fun scheduleDailyCheck(context: Context) {

        val request = PeriodicWorkRequestBuilder<DeadlineWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "deadline_worker",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }
}
