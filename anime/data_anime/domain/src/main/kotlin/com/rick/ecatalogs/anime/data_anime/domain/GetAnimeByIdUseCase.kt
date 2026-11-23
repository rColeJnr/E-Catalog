package com.rick.ecatalogs.anime.data_anime.domain

import com.rick.ecatalogs.anime.data_anime.data.repository.anime.AnimeByIdRepository
import com.rick.ecatalogs.anime.data_anime.data.repository.anime.UserAnimeDataRepository
import com.rick.ecatalogs.anime.data_anime.model.Anime
import com.rick.ecatalogs.anime.data_anime.model.AnimeUserData
import com.rick.ecatalogs.anime.data_anime.model.UserAnime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetAnimeByIdUseCase @Inject constructor(
    private val animeByIdRepository: AnimeByIdRepository,
    private val userDataRepository: UserAnimeDataRepository
) {

    operator fun invoke(id: Int): Flow<UserAnime> =
        animeByIdRepository.getAnimeById(id).mapToUserAnime(userDataRepository.animeUserData)

}

private fun Flow<Anime>.mapToUserAnime(userDataStream: Flow<AnimeUserData>): Flow<UserAnime> =
    combine(userDataStream) { anime, userData ->
        UserAnime(userData = userData, anime = anime)
    }