package com.rick.ecatalogs.data.sync.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.data.common.Dispatcher
import com.rick.ecatalogs.data.common.EcsDispatchers
import com.rick.ecatalogs.data.sync.initializers.SyncConstraints
import com.rick.ecatalogs.data.sync.status.SyncSubscriber
import com.rick.ecatalogs.movie.data_movie.data.repository.article.ArticleRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.TrendingMovieRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_series.TrendingSeriesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Syncs the first page of trending movies, trending series and articles. Each repository only
 * rewrites its list when the fetched ids differ from what is stored.
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val trendingMovieRepository: TrendingMovieRepository,
    private val trendingSeriesRepository: TrendingSeriesRepository,
    private val articleRepository: ArticleRepository,
    @Dispatcher(EcsDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
    private val syncSubscriber: SyncSubscriber,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        analyticsHelper.logSyncStarted()

        syncSubscriber.subscribe()

        // Sync the repositories in parallel. Each result is: changed (true/false), or a failure.
        val (movies, series, articles) = awaitAll(
            async { runCatching { trendingMovieRepository.syncFirstPage() } },
            async { runCatching { trendingSeriesRepository.syncFirstPage() } },
            async { runCatching { articleRepository.syncFirstPage() } },
        )
        val results = listOf(movies, series, articles)
        results.forEach { result ->
            result.exceptionOrNull()?.let { Log.e(TAG, "Sync failed", it) }
        }

        val syncedSuccessfully = results.all { it.isSuccess }
        analyticsHelper.logSyncFinished(syncedSuccessfully)

        // TODO: notify — post notifications for the lists that changed:
        //  movies.getOrDefault(false), series.getOrDefault(false), articles.getOrDefault(false)

        // Retrying is safe: repositories whose ids did not change are a no-op.
        if (syncedSuccessfully) Result.success() else Result.retry()
    }

    companion object {
        /**
         * One time work to sync data on app startup.
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()

        /**
         * Periodic work that keeps the lists current while the app isn't open.
         */
        fun periodicSyncWork() = PeriodicWorkRequestBuilder<DelegatingWorker>(
            SYNC_INTERVAL_HOURS, TimeUnit.HOURS,
        )
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }
}

private const val TAG = "SyncWorker"
private const val SYNC_INTERVAL_HOURS = 24L
