package com.rick.ecatalogs.book.screen_book.bestseller_catalog

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.ecatalogs.book.data_book.model.bestseller.UserBestseller

class BestsellerAdapter(
    private val onBookClick: (UserBestseller) -> Unit,
    private val onFavoriteClick: (View, String, Boolean) -> Unit,
) : PagingDataAdapter<UserBestseller, BestsellerViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: BestsellerViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(book = it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestsellerViewHolder {
        return BestsellerViewHolder.create(parent, onBookClick, onFavoriteClick)
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<UserBestseller>() {
            override fun areItemsTheSame(
                oldItem: UserBestseller,
                newItem: UserBestseller
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: UserBestseller,
                newItem: UserBestseller
            ): Boolean =
                oldItem == newItem

        }
    }
}

