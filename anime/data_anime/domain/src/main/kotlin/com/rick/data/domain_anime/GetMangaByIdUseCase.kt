package com.rick.data.domain_anime

import com.rick.anime.data_anime.data.repository.manga.MangaByIdRepository
import com.rick.anime.data_anime.data.repository.manga.UserMangaDataRepository
import com.rick.data.model_anime.Manga
import com.rick.data.model_anime.MangaUserData
import com.rick.data.model_anime.UserManga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMangaByIdUseCase @Inject constructor(
    private val mangaByIdRepository: MangaByIdRepository,
    private val userDataRepository: UserMangaDataRepository
) {

    operator fun invoke(id: Int): Flow<UserManga> {
        return mangaByIdRepository.getMangaById(id)
            .mapToUserManga(userDataRepository.mangaUserData)

    }
}

private fun Flow<Manga>.mapToUserManga(userDataStream: Flow<MangaUserData>): Flow<UserManga> =
    combine(userDataStream) { manga, userData ->
        UserManga(userData = userData, manga = manga)
    }
