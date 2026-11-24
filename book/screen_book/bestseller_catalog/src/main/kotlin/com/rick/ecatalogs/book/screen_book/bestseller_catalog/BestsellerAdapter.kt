package com.rick.ecatalogs.book.screen_book.bestseller_catalog

import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.ecatalogs.book.data_book.model.bestseller.UserBestseller

class BestsellerAdapter(
    private val onBookClick: (UserBestseller) -> Unit,
    private val onFavoriteClick: (View, String, Boolean) -> Unit,
    private val onTranslationClick: (UserBestseller, List<String>) -> Unit
) : PagingDataAdapter<UserBestseller, BestsellerViewHolder>(DIFF_UTIL) {

    private var lastPosition = -1

    override fun onBindViewHolder(holder: BestsellerViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(book = it)
            setAnimation(holder.itemView, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestsellerViewHolder {
        return BestsellerViewHolder.create(parent, onBookClick, onFavoriteClick, onTranslationClick)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.book_screen_book_bestseller_catalog_carousel_entry_anim)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
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

