package com.rick.screen_book.gutenberg_screen

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data.model_book.UserGutenberg
import com.rick.data.model_book.gutenberg.Formats

class BookCatalogAdapter(
    private val onItemClick: (view: View, formats: Formats) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit
) : PagingDataAdapter<UserGutenberg, BookCatalogViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: BookCatalogViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(book = it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookCatalogViewHolder {
        return BookCatalogViewHolder.create(parent, onItemClick, onFavClick)
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

