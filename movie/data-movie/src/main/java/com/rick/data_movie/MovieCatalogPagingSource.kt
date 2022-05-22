package com.rick.data_movie

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import java.io.IOException
import java.lang.Integer.max

private const val STARTING_PAGE = 1
private const val LOAD_DELAY_MILLIS = 3_000L
private const val ITEMS_PER_PAGE = 20

class MovieCatalogPagingSource(
    private val api: MovieCatalogApi,
    db: MovieCatalogDatabase,
) : PagingSource<Int, ResultDto>() {

    var off = 0
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResultDto> {
        // Start paging with the starting_key if this is the first load
        val position = params.key ?: STARTING_PAGE
        val movies = mutableListOf<ResultDto>()
        return try {
            off+=10
            val response = api.fetchMovieCatalog(off)
            val sublist = response.results.dropWhile { movies.contains(it) }
            movies.addAll(sublist)
            val nextKey = if (movies.isEmpty()) null
            else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (off /params.loadSize)
            }
            Log.w("taggoo", "$position and ${params.loadSize} and $off")
            LoadResult.Page(
                data = movies.toList(),
                prevKey = if (position == STARTING_PAGE) null else position - 1,
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