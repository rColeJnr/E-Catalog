package com.rick.ecatalogs.data.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.data.notifications.Notifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** Posts "<movie> is out today" on release day, then forgets the reminder. */
@HiltWorker
internal class ReleaseReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notifier: Notifier,
    private val preferences: PreferencesDataSource,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val movieId = inputData.getInt(KEY_MOVIE_ID, -1)
        val title = inputData.getString(KEY_TITLE).orEmpty()
        if (movieId == -1) return Result.success()
        notifier.postReleaseReminder(movieId, title)
        preferences.removeReleaseReminder(movieId)
        return Result.success()
    }

    companion object {
        internal const val KEY_MOVIE_ID = "movie_id"
        internal const val KEY_TITLE = "title"

        internal fun inputData(movieId: Int, title: String): Data = Data.Builder()
            .putAll(ReleaseReminderWorker::class.delegatedData())
            .putInt(KEY_MOVIE_ID, movieId)
            .putString(KEY_TITLE, title)
            .build()
    }
}
