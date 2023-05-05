package com.rick.screen_book.search_screen

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_book.favorite.Favorite
import com.rick.data_book.model.Formats
import com.rick.screen_book.BookCatalogAdapter
import com.rick.screen_book.BookCatalogViewHolder

class SearchAdapter(
    private val onItemClick: (view: View, formats: Formats) -> Unit ,
    private val onFavClick : (favorite: Favorite) -> Unit
): RecyclerView.Adapter<BookCatalogViewHolder>() {

    private val diffUtil = BookCatalogAdapter.DIFF_UTIL

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookCatalogViewHolder {
        return BookCatalogViewHolder.create(parent, onItemClick, onFavClick)
    }

    override fun onBindViewHolder(holder: BookCatalogViewHolder, position: Int) {
        val book = differ.currentList[position]
        holder.bind(book)
    }

    override fun getItemCount(): Int =
        differ.currentList.size
}
