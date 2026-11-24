package com.rick.ecatalogs.data.datastore

import com.rick.ecatalogs.data.datastore.test.InMemoryDataStore
import com.rick.ecatalogs.settings.data_settings.model.SYNC_INTERVAL_MONTH_HOURS
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListCacheTest {

    private lateinit var preferences: PreferencesDataSource
    private lateinit var subject: ListCache

    private val now = 1_800_000_000_000L
    private val day = TimeUnit.DAYS.toMillis(1)

    @Before
    fun setup() {
        preferences = PreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
        subject = ListCache(preferences)
    }

    @Test
    fun neverDownloadedList_isNotFresh() = runTest {
        assertFalse(subject.isFresh(CachedList.ANIME, now))
    }

    @Test
    fun list_isFreshForTheWeeklyIntervalByDefault() = runTest {
        subject.markRefreshed(CachedList.ANIME, now)

        assertTrue(subject.isFresh(CachedList.ANIME, now + 6 * day))
        assertFalse(subject.isFresh(CachedList.ANIME, now + 8 * day))
    }

    @Test
    fun monthlyInterval_keepsListsFreshLonger() = runTest {
        preferences.setSyncIntervalHours(SYNC_INTERVAL_MONTH_HOURS)
        subject.markRefreshed(CachedList.MANGA, now)

        assertTrue(subject.isFresh(CachedList.MANGA, now + 20 * day))
    }

    @Test
    fun lists_areTrackedSeparately() = runTest {
        subject.markRefreshed(CachedList.bestseller("business-books"), now)

        assertTrue(subject.isFresh(CachedList.bestseller("business-books"), now))
        assertFalse(subject.isFresh(CachedList.bestseller("graphic-books-and-manga"), now))
    }

    @Test
    fun timestampInTheFuture_isNotTrusted() = runTest {
        // e.g. the phone's clock was moved back
        subject.markRefreshed(CachedList.GUTENBERG, now + day)
        assertFalse(subject.isFresh(CachedList.GUTENBERG, now))
    }

    @Test
    fun exclusiveRefresh_leavesOnlyTheStoredFilterFresh() = runTest {
        subject.markRefreshedExclusive("gutenberg:all:all", prefix = "gutenberg", now = now)
        subject.markRefreshedExclusive("gutenberg:pt:poetry", prefix = "gutenberg", now = now)

        assertTrue(subject.isFresh("gutenberg:pt:poetry", now))
        assertFalse(subject.isFresh("gutenberg:all:all", now))
    }

    @Test
    fun exclusiveRefresh_doesNotTouchOtherLists() = runTest {
        subject.markRefreshed(CachedList.ANIME, now)
        subject.markRefreshedExclusive("gutenberg:all:all", prefix = "gutenberg", now = now)

        assertTrue(subject.isFresh(CachedList.ANIME, now))
    }

    @Test
    fun dailyList_isFreshOnlyOnTheSameDay() = runTest {
        val morning = java.time.LocalDate.of(2026, 11, 24)
            .atTime(8, 0).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        subject.markRefreshed(CachedList.AIRING_TODAY_SERIES, morning)

        assertTrue(subject.isFreshToday(CachedList.AIRING_TODAY_SERIES, morning + 10 * hour))
        assertFalse(subject.isFreshToday(CachedList.AIRING_TODAY_SERIES, morning + day))
    }

    private val hour = TimeUnit.HOURS.toMillis(1)
}
