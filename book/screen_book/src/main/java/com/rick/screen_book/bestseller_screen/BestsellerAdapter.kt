package com.rick.screen_book.bestseller_screen

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_book.UserBestseller

class BestsellerAdapter(
    private val onBookClick: (View, UserBestseller) -> Unit,
    private val onFavoriteClick: (View, String, Boolean) -> Unit,
) : RecyclerView.Adapter<BestsellerViewHolder>() {

    val differ = AsyncListDiffer(this, DIFF_UTIL)

    override fun onBindViewHolder(holder: BestsellerViewHolder, position: Int) {
        differ.currentList.get(position)?.let {
            holder.bind(book = it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestsellerViewHolder {
        return BestsellerViewHolder.create(parent, onBookClick, onFavoriteClick)
    }

    override fun getItemCount(): Int = differ.currentList.size

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<UserBestseller>() {
            override fun areItemsTheSame(
                oldItem: UserBestseller,
                newItem: UserBestseller
            ): Boolean =
                oldItem.rank == newItem.rank

            override fun areContentsTheSame(
                oldItem: UserBestseller,
                newItem: UserBestseller
            ): Boolean =
                oldItem == newItem

        }
    }
}

