package com.rick.ecatalogs.settings.data_settings.data.repository

import com.rick.ecatalogs.data.analytics.AnalyticsEvent
import com.rick.ecatalogs.data.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logUsernameChanged(username: String) =
    logEvent(
        AnalyticsEvent(
            type = "username_changed",
            extras = listOf(
                AnalyticsEvent.Param(key = "username_name", value = username),
            ),
        ),
    )