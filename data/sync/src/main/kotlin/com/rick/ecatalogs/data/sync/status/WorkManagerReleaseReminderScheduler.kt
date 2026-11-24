package com.rick.ecatalogs.data.sync.status

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rick.ecatalogs.data.sync.workers.DelegatingWorker
import com.rick.ecatalogs.data.sync.workers.ReleaseReminderWorker
import com.rick.ecatalogs.data.util.ReleaseReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/** One WorkManager job per movie, at 9:00 local time on its release day. */
internal class WorkManagerReleaseReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : ReleaseReminderScheduler {

    override fun schedule(movieId: Int, title: String, releaseDate: LocalDate) {
        val at = LocalDateTime.of(releaseDate, REMIND_AT)
        val delay = Duration.between(LocalDateTime.now(), at).toMillis().coerceAtLeast(0)
        val request = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(ReleaseReminderWorker.inputData(movieId, title))
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(workName(movieId), ExistingWorkPolicy.REPLACE, request)
    }

    override fun cancel(movieId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(movieId))
    }

    private fun workName(movieId: Int) = "release_reminder_$movieId"
}

private val REMIND_AT: LocalTime = LocalTime.of(9, 0)
