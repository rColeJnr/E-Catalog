package com.rick.screen_book.bestseller_screen

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data_book.nytimes.model.NYBook

class BestsellerAdapter(
    private val onBookClick: (book: NYBook) -> Unit,
    private val onFavoriteClick: (book: NYBook) -> Unit,
): PagingDataAdapter<NYBook, BestsellerViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: BestsellerViewHolder, position: Int) {
        val book = getItem(position)
        if (book != null) {
            holder.bind(book = book)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestsellerViewHolder {
        return BestsellerViewHolder.create(parent, onBookClick, onFavoriteClick)
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<NYBook>() {
            override fun areItemsTheSame(oldItem: NYBook, newItem: NYBook): Boolean =
                oldItem.rank == newItem.rank

            override fun areContentsTheSame(oldItem: NYBook, newItem: NYBook): Boolean =
                oldItem == newItem

        }
    }
}

