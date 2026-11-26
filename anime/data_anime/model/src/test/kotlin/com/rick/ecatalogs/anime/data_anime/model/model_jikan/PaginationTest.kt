package com.rick.ecatalogs.anime.data_anime.model.model_jikan

import com.google.gson.Gson
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Retrofit parses Jikan with Gson: the snake_case fields must map (see @SerializedName). */
class PaginationTest {

    private val gson = Gson()

    @Test
    fun jikanPagination_isReadByGson() {
        val pagination = gson.fromJson(
            """
            {"last_visible_page": 41, "has_next_page": true, "current_page": 2,
             "items": {"count": 25, "total": 1012, "per_page": 25}}
            """,
            Pagination::class.java,
        )

        assertTrue(pagination.hasNextPage)
        assertEquals(2, pagination.currentPage)
        assertEquals(41, pagination.lastVisiblePage)
        assertEquals(25, pagination.items.perPage)
        assertEquals(1012, pagination.items.total)
    }

    @Test
    fun lastPage_hasNoNextPage() {
        val pagination = gson.fromJson("""{"has_next_page": false}""", Pagination::class.java)

        assertFalse(pagination.hasNextPage)
    }
}
