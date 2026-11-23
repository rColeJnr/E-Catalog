package com.rick.ecatalogs.anime.data_anime.domain

import com.rick.ecatalogs.anime.data_anime.data.repository.manga.MangaByIdRepository
import com.rick.ecatalogs.anime.data_anime.data.repository.manga.UserMangaDataRepository
import com.rick.ecatalogs.anime.data_anime.model.Manga
import com.rick.ecatalogs.anime.data_anime.model.MangaUserData
import com.rick.ecatalogs.anime.data_anime.model.UserManga
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
