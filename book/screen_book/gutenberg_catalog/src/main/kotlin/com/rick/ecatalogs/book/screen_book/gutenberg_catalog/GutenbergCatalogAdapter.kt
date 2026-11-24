package com.rick.ecatalogs.book.screen_book.gutenberg_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.book.screen_book.gutenberg_catalog.GutenbergCatalogViewHolder
import com.rick.ecatalogs.book.screen_book.gutenberg_catalog.R
import com.rick.ecatalogs.book.data_book.model.gutenberg.UserGutenberg

class GutenbergCatalogAdapter(
    private val onItemClick: (view: View, book: UserGutenberg) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit
) : PagingDataAdapter<UserGutenberg, RecyclerView.ViewHolder>(DIFF_UTIL) {

    // The header (title and filters) is a separate adapter in a ConcatAdapter, so positions here
    // are the books' own positions.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        GutenbergCatalogViewHolder.create(parent, onItemClick, onFavClick)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as GutenbergCatalogViewHolder).bind(it) }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<UserGutenberg>() {
            override fun areItemsTheSame(oldItem: UserGutenberg, newItem: UserGutenberg): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserGutenberg, newItem: UserGutenberg): Boolean =
                oldItem == newItem
        }
    }
}
