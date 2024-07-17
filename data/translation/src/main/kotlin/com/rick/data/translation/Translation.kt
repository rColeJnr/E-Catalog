package com.rick.data.translation

import kotlinx.serialization.Serializable

@Serializable
data class Translation(
    val text: String, val detectedLanguageCode: String
)