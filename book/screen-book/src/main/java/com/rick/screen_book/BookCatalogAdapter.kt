package com.rick.screen_book

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data_book.model.Book

class BookCatalogAdapter : PagingDataAdapter<Book, BookCatalogViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: BookCatalogViewHolder, position: Int) {
        val book = getItem(position)!!
        holder.bind(book = book)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookCatalogViewHolder {
        TODO("Not yet implemented")
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
                oldItem == newItem

        }
    }
}

