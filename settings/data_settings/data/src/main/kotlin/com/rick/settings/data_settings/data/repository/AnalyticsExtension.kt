package com.rick.settings.data_settings.data.repository

import com.rick.data.analytics.AnalyticsEvent
import com.rick.data.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logUsernameChanged(username: String) =
    logEvent(
        AnalyticsEvent(
            type = "username_changed",
            extras = listOf(
                AnalyticsEvent.Param(key = "username_name", value = username),
            ),
        ),
    )