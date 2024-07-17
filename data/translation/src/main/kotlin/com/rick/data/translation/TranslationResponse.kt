package com.rick.data.translation

import kotlinx.serialization.Serializable

@Serializable
data class TranslationResponse(
    val translations: List<Translation>
)

