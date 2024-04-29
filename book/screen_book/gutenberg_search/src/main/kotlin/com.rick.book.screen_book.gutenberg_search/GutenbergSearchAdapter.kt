package com.rick.book.screen_book.gutenberg_search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_book.gutenberg.Formats
import com.rick.data.model_book.gutenberg.UserGutenberg

class GutenbergSearchAdapter(
    private val onItemClick: (view: View, formats: Formats) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {

    private val diffUtil = DIFF_UTIL

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.create(parent, onItemClick, onFavClick)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val book = differ.currentList[position]
        holder.bind(book)
    }

    override fun getItemCount(): Int =
        differ.currentList.size

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
