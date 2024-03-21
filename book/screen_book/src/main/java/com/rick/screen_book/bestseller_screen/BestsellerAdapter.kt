package com.rick.screen_book.bestseller_screen

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data_book.nytimes.model.NYBook

class BestsellerAdapter(
    private val onBookClick: (View, NYBook) -> Unit,
    private val onFavoriteClick: (View, NYBook) -> Unit,
): PagingDataAdapter<NYBook, BestsellerViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: BestsellerViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(book = it)
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

