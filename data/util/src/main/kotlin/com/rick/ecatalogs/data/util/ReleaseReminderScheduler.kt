package com.rick.ecatalogs.data.util

import java.time.LocalDate

/** Schedules a notification on a movie's release day (implemented with WorkManager). */
interface ReleaseReminderScheduler {
    fun schedule(movieId: Int, title: String, releaseDate: LocalDate)

    fun cancel(movieId: Int)
}
