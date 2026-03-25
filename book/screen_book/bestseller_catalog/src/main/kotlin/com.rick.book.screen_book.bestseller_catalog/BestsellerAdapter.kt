package com.rick.book.screen_book.bestseller_catalog

import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_book.bestseller.UserBestseller

class BestsellerAdapter(
    private val onBookClick: (UserBestseller) -> Unit,
    private val onFavoriteClick: (View, String, Boolean) -> Unit,
    private val onTranslationClick: (UserBestseller, List<String>) -> Unit
) : RecyclerView.Adapter<BestsellerViewHolder>() {

    val differ = AsyncListDiffer(this, DIFF_UTIL)
    private var lastPosition = -1

    override fun onBindViewHolder(holder: BestsellerViewHolder, position: Int) {
        differ.currentList[position]?.let {
            holder.bind(book = it)
            setAnimation(holder.itemView, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestsellerViewHolder {
        return BestsellerViewHolder.create(parent, onBookClick, onFavoriteClick, onTranslationClick)
    }

    override fun getItemCount(): Int = differ.currentList.size

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
                oldItem.rank == newItem.rank

            override fun areContentsTheSame(
                oldItem: UserBestseller,
                newItem: UserBestseller
            ): Boolean =
                oldItem == newItem

        }
    }
}

