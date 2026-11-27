package com.rick.ecatalogs.data.notifications

import com.rick.ecatalogs.anime.data_anime.model.Anime
import com.rick.ecatalogs.anime.data_anime.model.Manga
import com.rick.ecatalogs.game.data_game.model.Game
import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.rick.ecatalogs.book.data_book.model.bestseller.Bestseller
import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerGenre
import com.rick.ecatalogs.movie.data_movie.model.article_models.Article
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [Notifier] that shows one notification per category in the system tray. Tapping it opens the
 * category's list through a deep link handled by MainActivity's navigation graph.
 */
@Singleton
internal class SystemNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {

    override fun postTrendingMoviesNotifications(movies: List<TrendingMovie>) = post(
        channel = Channel.TRENDING_MOVIES,
        notificationId = TRENDING_MOVIES_ID,
        title = context.getString(R.string.data_notifications_new_trending_movies),
        lines = movies.map(TrendingMovie::title),
        deepLink = DeepLinks.TRENDING_MOVIE_CATALOG,
    )

    override fun postTrendingSeriesNotifications(series: List<TrendingSeries>) = post(
        channel = Channel.TRENDING_SERIES,
        notificationId = TRENDING_SERIES_ID,
        title = context.getString(R.string.data_notifications_new_trending_series),
        lines = series.map(TrendingSeries::name),
        deepLink = DeepLinks.TRENDING_SERIES_CATALOG,
    )

    override fun postArticlesNotifications(articles: List<Article>) = post(
        channel = Channel.ARTICLES,
        notificationId = ARTICLES_ID,
        title = context.getString(R.string.data_notifications_new_articles),
        lines = articles.map(Article::headline),
        deepLink = DeepLinks.ARTICLE_CATALOG,
    )

    override fun postBestsellersNotification(genre: BestsellerGenre, books: List<Bestseller>) = post(
        channel = Channel.BESTSELLERS,
        // One notification per genre, so each opens its own list
        notificationId = BESTSELLERS_BASE_ID + genre.ordinal,
        title = context.getString(
            R.string.data_notifications_new_bestsellers,
            context.getString(genre.labelRes),
        ),
        lines = books.map { "${it.title} – ${it.author}" },
        deepLink = DeepLinks.bestsellerCatalog(genre),
        group = BESTSELLERS_GROUP,
    )

    override fun postComingSoonGamesNotification(games: List<Game>) = post(
        channel = Channel.GAMES,
        notificationId = GAMES_ID,
        title = context.getString(R.string.data_notifications_new_coming_soon_games),
        lines = games.map(Game::name),
        deepLink = DeepLinks.GAME_CATALOG,
    )

    override fun postAnimeNotifications(anime: List<Anime>) = post(
        channel = Channel.ANIME,
        notificationId = ANIME_ID,
        title = context.getString(R.string.data_notifications_new_anime),
        lines = anime.map(Anime::title),
        deepLink = DeepLinks.ANIME_CATALOG,
    )

    override fun postMangaNotifications(manga: List<Manga>) = post(
        channel = Channel.MANGA,
        notificationId = MANGA_ID,
        title = context.getString(R.string.data_notifications_new_manga),
        lines = manga.map(Manga::title),
        deepLink = DeepLinks.MANGA_CATALOG,
    )

    override fun postReleaseReminder(movieId: Int, title: String) {
        if (!canPostNotifications()) return
        ensureChannelExists(Channel.RELEASE_REMINDERS)
        val notificationId = RELEASE_REMINDER_BASE_ID + movieId
        val notification = NotificationCompat.Builder(context, Channel.RELEASE_REMINDERS.id)
            .setSmallIcon(R.drawable.data_notifications_ic_notification)
            .setContentTitle(context.getString(R.string.data_notifications_release_today, title))
            .setContentText(context.getString(R.string.data_notifications_release_today_text))
            .setContentIntent(deepLinkIntent(DeepLinks.movieDetails(movieId), notificationId))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun post(
        channel: Channel,
        notificationId: Int,
        title: String,
        lines: List<String>,
        deepLink: String,
        group: String? = null,
    ) {
        if (lines.isEmpty() || !canPostNotifications()) return
        ensureChannelExists(channel)

        val shown = lines.take(MAX_LINES)
        val summary = context.resources.getQuantityString(
            R.plurals.data_notifications_summary, lines.size, lines.size,
        )
        val style = shown
            .fold(NotificationCompat.InboxStyle()) { style, line -> style.addLine(line) }
            .setBigContentTitle(title)
            .setSummaryText(summary)

        val notification: Notification = NotificationCompat.Builder(context, channel.id)
            .setSmallIcon(R.drawable.data_notifications_ic_notification)
            .setContentTitle(title)
            .setContentText(shown.joinToString())
            .setStyle(style)
            .setNumber(lines.size)
            .setContentIntent(deepLinkIntent(deepLink, notificationId))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .apply { if (group != null) setGroup(group) }
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun canPostNotifications(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED
        ) return false
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun ensureChannelExists(channel: Channel) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val notificationChannel = NotificationChannel(
            channel.id,
            context.getString(channel.nameRes),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.data_notifications_channel_description)
        }
        NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
    }

    private fun deepLinkIntent(deepLink: String, requestCode: Int): PendingIntent =
        PendingIntent.getActivity(
            context,
            requestCode,
            Intent(Intent.ACTION_VIEW, deepLink.toUri()).apply {
                component = ComponentName(context.packageName, TARGET_ACTIVITY_NAME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private enum class Channel(val id: String, @StringRes val nameRes: Int) {
        TRENDING_MOVIES("trending_movies", R.string.data_notifications_channel_trending_movies),
        TRENDING_SERIES("trending_series", R.string.data_notifications_channel_trending_series),
        ARTICLES("articles", R.string.data_notifications_channel_articles),
        BESTSELLERS("bestsellers", R.string.data_notifications_channel_bestsellers),
        RELEASE_REMINDERS("release_reminders", R.string.data_notifications_channel_release_reminders),
        GAMES("games", R.string.data_notifications_channel_games),
        ANIME("anime", R.string.data_notifications_channel_anime),
        MANGA("manga", R.string.data_notifications_channel_manga),
    }
}

@get:StringRes
private val BestsellerGenre.labelRes: Int
    get() = when (this) {
        BestsellerGenre.FICTION -> R.string.data_notifications_genre_fiction
        BestsellerGenre.NONFICTION -> R.string.data_notifications_genre_nonfiction
        BestsellerGenre.MISCELLANEOUS -> R.string.data_notifications_genre_miscellaneous
        BestsellerGenre.BUSINESS -> R.string.data_notifications_genre_business
        BestsellerGenre.GRAPHIC -> R.string.data_notifications_genre_graphic
    }

private const val TARGET_ACTIVITY_NAME = "com.rick.ecatalogs.MainActivity"
private const val MAX_LINES = 5
private const val TRENDING_MOVIES_ID = 1001
private const val TRENDING_SERIES_ID = 1002
private const val ARTICLES_ID = 1003
private const val GAMES_ID = 1004
private const val ANIME_ID = 1005
private const val MANGA_ID = 1006
private const val BESTSELLERS_BASE_ID = 1100
private const val BESTSELLERS_GROUP = "bestsellers"
private const val RELEASE_REMINDER_BASE_ID = 200_000
