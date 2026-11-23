package com.rick.ecatalogs.data.sync.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.BestsellerRepository
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.data.common.Dispatcher
import com.rick.ecatalogs.data.common.EcsDispatchers
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.data.notifications.Notifier
import com.rick.ecatalogs.data.sync.initializers.SyncConstraints
import com.rick.ecatalogs.data.sync.status.SyncSubscriber
import com.rick.ecatalogs.movie.data_movie.data.repository.article.ArticleRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.TrendingMovieRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_series.TrendingSeriesRepository
import com.rick.ecatalogs.settings.data_settings.model.DEFAULT_SYNC_INTERVAL_HOURS
import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Syncs the first page of trending movies, trending series and articles, plus every bestseller
 * list. Each repository only rewrites its list when the content changed, and returns what's new,
 * which is posted as one notification per category (if that category's notifications are on).
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val trendingMovieRepository: TrendingMovieRepository,
    private val trendingSeriesRepository: TrendingSeriesRepository,
    private val articleRepository: ArticleRepository,
    private val bestsellerRepository: BestsellerRepository,
    private val preferences: PreferencesDataSource,
    private val notifier: Notifier,
    @Dispatcher(EcsDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
    private val syncSubscriber: SyncSubscriber,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val settings = preferences.settingsUserData.first()
        // The user turned background sync off; the periodic job is cancelled from Settings, but a
        // run may already be queued.
        if (!settings.syncEnabled) return@withContext Result.success()

        analyticsHelper.logSyncStarted()
        syncSubscriber.subscribe()

        // Sync the repositories in parallel
        val movies = async { runCatching { trendingMovieRepository.syncFirstPage() } }
        val series = async { runCatching { trendingSeriesRepository.syncFirstPage() } }
        val articles = async { runCatching { articleRepository.syncFirstPage() } }
        val bestsellers = async { runCatching { bestsellerRepository.syncAllGenres() } }
        val results = listOf(movies.await(), series.await(), articles.await(), bestsellers.await())

        results.forEach { result ->
            result.exceptionOrNull()?.let { Log.e(TAG, "Sync failed", it) }
        }
        val syncedSuccessfully = results.all { it.isSuccess }
        analyticsHelper.logSyncFinished(syncedSuccessfully)

        val enabled = settings.enabledNotifications
        if (NotificationCategory.TRENDING_MOVIES in enabled) {
            movies.await().getOrNull()?.let(notifier::postTrendingMoviesNotifications)
        }
        if (NotificationCategory.TRENDING_SERIES in enabled) {
            series.await().getOrNull()?.let(notifier::postTrendingSeriesNotifications)
        }
        if (NotificationCategory.ARTICLES in enabled) {
            articles.await().getOrNull()?.let(notifier::postArticlesNotifications)
        }
        if (NotificationCategory.BESTSELLERS in enabled) {
            bestsellers.await().getOrNull()?.forEach { (genre, books) ->
                notifier.postBestsellersNotification(genre, books)
            }
        }

        // Retrying is safe: repositories whose content didn't change are a no-op.
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
        fun periodicSyncWork(intervalHours: Int = DEFAULT_SYNC_INTERVAL_HOURS) =
            PeriodicWorkRequestBuilder<DelegatingWorker>(intervalHours.toLong(), TimeUnit.HOURS)
                .setConstraints(SyncConstraints)
                .setInputData(SyncWorker::class.delegatedData())
                .build()
    }
}

private const val TAG = "SyncWorker"
