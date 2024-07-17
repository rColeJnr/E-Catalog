package com.rick.anime.anime_screen.common

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.domain_anime.GetTranslationUseCase
import com.rick.data.translation.Translation
import com.rick.data.translation.TranslationBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TranslationViewModel @Inject constructor(
    private val getTranslationUseCase: GetTranslationUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val location = savedStateHandle.getStateFlow(LOCATION_QUERY, "")

    var translation: Flow<List<Translation>> = flowOf()
        private set

    fun onEvent(event: TranslationEvent) {
        when (event) {
            is TranslationEvent.GetTranslation -> translation =
                getTranslation(event.texts, event.lCode)

        }
    }

    private fun getTranslation(texts: List<String>, lCode: String): Flow<List<Translation>> =
        getTranslationUseCase.invoke(
            TranslationBody(
                folderId = FOLDER_ID,
                targetLanguageCode = lCode,
                texts = texts
            )
        )

    fun setLocation(location: String) {
        viewModelScope.launch {
            savedStateHandle[LOCATION_QUERY] = location
        }
    }
}

private const val LOCATION_QUERY = "LocationQuery"
private const val FOLDER_ID: String = "b1g3qbtiprdplbrpod5i"

sealed interface TranslationEvent {
    data class GetTranslation(val texts: List<String>, val lCode: String) : TranslationEvent
}