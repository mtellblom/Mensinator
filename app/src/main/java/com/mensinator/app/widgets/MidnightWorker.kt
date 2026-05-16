package com.mensinator.app.widgets

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.mensinator.app.BuildConfig
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay

class MidnightWorker(val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        fun scheduleNextMidnight(context: Context) {
            val delay = if (BuildConfig.DEBUG) {
                WidgetDebugPrefs.getIntervalMillis(context)
            } else {
                val now = ZonedDateTime.now()
                Duration.between(now, now.plusDays(1).with(LocalTime.MIDNIGHT)).toMillis()
            }

            val request = OneTimeWorkRequestBuilder<MidnightWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("midnight_refresh")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "midnight_refresh",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    override suspend fun doWork(): Result {
        try {
            // Wait for 1 second to ensure the worker has time to start
            delay(1000)
            MidnightTrigger.midnightTrigger.emit(Unit)
            WidgetInstances.forEach { it.glanceAppWidget.updateAll(context) }
        } catch (e: Exception) {
            android.util.Log.e("MidnightWorker", "Failed to refresh widgets at midnight", e)
        } finally {
            // Always schedule the next midnight, even if this run failed,
            // so the daily cycle keeps going.
            scheduleNextMidnight(applicationContext)
        }
        return Result.success()
    }
}