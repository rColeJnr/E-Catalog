package com.rick.data_movie

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1
private const val LOAD_DELAY_MILLIS = 3_000L
private const val ITEMS_PER_PAGE = 20
/*
    network items count, refer to documentation at https://developer.nytimes.com/docs/movie-reviews-api/1/routes/reviews/search.json/get
 */
var offset = 20
class MovieCatalogPagingSource(
    private val api: MovieCatalogApi,
    db: MovieCatalogDatabase,
) : PagingSource<Int, ResultDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResultDto> {
        // Start paging with the starting_key if this is the first load
        val position = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = api.fetchMovieCatalog(offset)
            offset+=20
            val movies = response.results
            val nextKey = if (movies.isEmpty()) null
            else {
                position + offset / ITEMS_PER_PAGE
            }
            LoadResult.Page(
                data = movies,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        }
        catch (e: IOException) {
            return LoadResult.Error(e)
        }
        catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ResultDto>): Int? {
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    /**
     * Makes sure the paging key is never less than [STARTING_KEY]
     */
//    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)
}