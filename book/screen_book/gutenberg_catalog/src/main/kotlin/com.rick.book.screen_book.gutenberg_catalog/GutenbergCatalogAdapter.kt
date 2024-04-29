package com.rick.book.screen_book.gutenberg_catalog

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data.model_book.gutenberg.Formats
import com.rick.data.model_book.gutenberg.UserGutenberg

class GutenbergCatalogAdapter(
    private val onItemClick: (view: View, formats: Formats) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit
) : PagingDataAdapter<UserGutenberg, GutenbergCatalogViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: GutenbergCatalogViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(book = it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GutenbergCatalogViewHolder {
        return GutenbergCatalogViewHolder.create(parent, onItemClick, onFavClick)
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<UserGutenberg>() {
            override fun areItemsTheSame(oldItem: UserGutenberg, newItem: UserGutenberg): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: UserGutenberg,
                newItem: UserGutenberg
            ): Boolean =
                oldItem == newItem

        }
    }
}

