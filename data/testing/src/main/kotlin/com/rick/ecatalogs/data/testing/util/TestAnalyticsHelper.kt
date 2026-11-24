package com.rick.ecatalogs.data.testing.util

import com.rick.ecatalogs.data.analytics.AnalyticsEvent
import com.rick.ecatalogs.data.analytics.AnalyticsHelper

class TestAnalyticsHelper : AnalyticsHelper {

    private val events = mutableListOf<AnalyticsEvent>()

    override fun logEvent(event: AnalyticsEvent) {
        events.add(event)
    }

    fun hasLogged(event: AnalyticsEvent) = event in events

    fun hasLoggedType(type: String) = events.any { it.type == type }
}
