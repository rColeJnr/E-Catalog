package com.rick.data_movie

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import java.io.IOException
import java.lang.Integer.max

private const val STARTING_PAGE_INDEX = 1
private const val LOAD_DELAY_MILLIS = 3_000L
var ITEMS_PER_PAGE = 0
var offset = 20
class MovieCatalogPagingSource(
    private val api: MovieCatalogApi,
    db: MovieCatalogDatabase,
) : PagingSource<Int, ResultDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResultDto> {
        // Start paging with the starting_key if this is the first load
        val position = params.key ?: 1
        val movies = mutableListOf<ResultDto>()
        return try {
            val response = api.fetchMovieCatalog(offset)
            offset+=20
            val result = response.results
            val nextKey = if (result.isEmpty()) null
            else {
                position + params.loadSize / 20
            }
            Log.w("taggoo", "$position and ${params.loadSize} and $ITEMS_PER_PAGE")
            LoadResult.Page(
                data = result.sortedBy { it.display_title },
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