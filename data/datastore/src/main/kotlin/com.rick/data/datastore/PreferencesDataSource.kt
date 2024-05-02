package com.rick.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.rick.data.model_anime.AnimeUserData
import com.rick.data.model_anime.MangaUserData
import com.rick.data.model_book.bestseller.BestsellerUserData
import com.rick.data.model_book.gutenberg.GutenbergUserData
import com.rick.data.model_movie.ArticleUserData
import com.rick.data.model_movie.TrendingMovieUserData
import com.rick.data.model_movie.TrendingSeriesUserData
import com.rick.settings.data_settings.model.SettingsUserData
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class PreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    //
    val settingsUserData = userPreferences.data
        .map {
            SettingsUserData(
                userName = it.userName
            )
        }

    val animeUserData = userPreferences.data
        .map {
            AnimeUserData(
                animeFavoriteIds = it.animeFavoriteIdsMap.keys
            )
        }

    val mangaUserData = userPreferences.data
        .map {
            MangaUserData(
                mangaFavoriteIds = it.mangaFavoriteIdsMap.keys
            )
        }

    val bestsellerUserData = userPreferences.data
        .map {
            BestsellerUserData(
                bestsellerFavoriteIds = it.bestsellerFavoriteIdsMap.keys,
            )
        }

    val gutenbergUserData = userPreferences.data
        .map {
            GutenbergUserData(
                gutenbergFavoriteIds = it.gutenbergFavoriteIdsMap.keys,
            )
        }

    val articleUserData = userPreferences.data
        .map {
            ArticleUserData(
                nyTimesArticlesFavoriteIds = it.nyTimesArticlesFavoriteIdsMap.keys,
            )
        }

    val trendingMovieUserData = userPreferences.data
        .map {
            TrendingMovieUserData(
                trendingMovieFavoriteIds = it.trendingMovieFavoriteIdsMap.keys,
            )
        }

    val trendingSeriesUserData = userPreferences.data
        .map {
            TrendingSeriesUserData(
                trendingSeriesFavoriteIds = it.trendingSeriesFavoriteIdsMap.keys,
            )
        }

    suspend fun setBestsellerFavoriteIds(bestsellerId: String, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        bestsellerFavoriteIds.put(bestsellerId, true)
                    } else {
                        bestsellerFavoriteIds.remove(bestsellerId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setGutenbergFavoriteIds(gutenbergId: Int, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        gutenbergFavoriteIds.put(gutenbergId, true)
                    } else {
                        gutenbergFavoriteIds.remove(gutenbergId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setTrendingMovieFavoriteIds(movieId: Int, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        trendingMovieFavoriteIds.put(movieId, true)
                    } else {
                        trendingMovieFavoriteIds.remove(movieId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setTrendingSeriesFavoriteIds(seriesId: Int, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        trendingSeriesFavoriteIds.put(seriesId, true)
                    } else {
                        trendingSeriesFavoriteIds.remove(seriesId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setNyTimesArticlesFavoriteIds(articleId: String, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        nyTimesArticlesFavoriteIds.put(articleId, true)
                    } else {
                        nyTimesArticlesFavoriteIds.remove(articleId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setAnimeFavoriteIds(animeId: Int, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        animeFavoriteIds.put(animeId, true)
                    } else {
                        animeFavoriteIds.remove(animeId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setMangaFavoriteIds(mangaId: Int, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        mangaFavoriteIds.put(mangaId, true)
                    } else {
                        mangaFavoriteIds.remove(mangaId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setUsername(name: String) {
        try {
            userPreferences.updateData {
                it.copy {
                    userName = name
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }
}