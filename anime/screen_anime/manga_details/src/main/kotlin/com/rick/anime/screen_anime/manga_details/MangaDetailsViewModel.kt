package com.rick.anime.screen_anime.manga_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rick.data.domain_anime.GetMangaByIdUseCase
import com.rick.data.model_anime.UserManga
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    private val getMangaByIdUseCase: GetMangaByIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // WHY do i need a viewModel? besides survive process death?
    private val mangaId = savedStateHandle.getStateFlow(key = MANGA_ID, initialValue = -1)

    fun setMangaId(id: Int) {
        savedStateHandle[MANGA_ID] = id
    }

    fun getManga(): Flow<UserManga> =
        getMangaByIdUseCase(mangaId.value)

}

private const val MANGA_ID = "manga"