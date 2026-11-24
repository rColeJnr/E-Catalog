package com.rick.ecatalogs.movie.data_movie.data.repository.browse

import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.data.util.ReleaseReminderScheduler
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

/** "Remind me on release day" for upcoming movies. */
interface ReleaseReminderRepository {
    /** Ids of movies with a reminder. */
    val reminderIds: Flow<Set<Int>>

    /** Adds a reminder (scheduled for the release date) or removes the existing one. */
    suspend fun toggleReminder(movie: TrendingMovie)
}

internal class ReleaseReminderRepositoryImpl @Inject constructor(
    private val preferences: PreferencesDataSource,
    private val scheduler: ReleaseReminderScheduler,
) : ReleaseReminderRepository {

    override val reminderIds: Flow<Set<Int>> = preferences.releaseReminderIds

    override suspend fun toggleReminder(movie: TrendingMovie) {
        val releaseDate = try {
            LocalDate.parse(movie.releaseDate)
        } catch (e: DateTimeParseException) {
            return // no date to remind on
        }
        if (movie.id in preferences.releaseReminderIdsNow()) {
            preferences.removeReleaseReminder(movie.id)
            scheduler.cancel(movie.id)
        } else {
            preferences.setReleaseReminder(movie.id, movie.title, movie.releaseDate)
            scheduler.schedule(movie.id, movie.title, releaseDate)
        }
    }
}
