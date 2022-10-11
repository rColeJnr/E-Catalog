package com.rick.screen_book

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_book.model.Book

class BookCatalogAdapter : RecyclerView.Adapter<BookCatalogViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
            oldItem == newItem

    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookCatalogViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: BookCatalogViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}

