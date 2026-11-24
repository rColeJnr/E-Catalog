package com.rick.ecatalogs.book.data_book.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * NYT Books `lists/overview.json`: the top entries of every bestseller list in one response.
 * Only what the sync needs to detect changes is kept (list name, ranked ISBNs).
 */
@Serializable
data class BestsellerOverviewResponse(
    val results: OverviewResults? = null,
)

@Serializable
data class OverviewResults(
    val lists: List<OverviewList>? = null,
)

@Serializable
data class OverviewList(
    /** Same name as used by `lists/{date}/{list}.json`, e.g. "business-books". */
    @SerialName("list_name_encoded")
    @SerializedName("list_name_encoded")
    val listNameEncoded: String? = null,
    val books: List<OverviewBook>? = null,
)

@Serializable
data class OverviewBook(
    @SerialName("primary_isbn13")
    @SerializedName("primary_isbn13")
    val id: String? = null,
    val rank: Int? = null,
)

/** Ranked book ids of each list, keyed by list name. */
fun BestsellerOverviewResponse.rankedIdsByList(): Map<String, List<String>> =
    results?.lists.orEmpty()
        .mapNotNull { list ->
            val name = list.listNameEncoded ?: return@mapNotNull null
            name to list.books.orEmpty()
                .filter { !it.id.isNullOrBlank() }
                .sortedBy { it.rank ?: Int.MAX_VALUE }
                .map { it.id!! }
        }
        .toMap()
