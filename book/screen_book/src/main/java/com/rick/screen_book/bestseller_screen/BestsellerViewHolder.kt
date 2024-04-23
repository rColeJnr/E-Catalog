package com.rick.screen_book.bestseller_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_book.bestseller.UserBestseller
import com.rick.screen_book.R
import com.rick.screen_book.databinding.BookEntryBinding
import com.rick.screen_book.gutenberg_screen.provideGlide

class BestsellerViewHolder(
    binding: BookEntryBinding,
    private val onBookClick: (View, UserBestseller) -> Unit,
    private val onFavoriteClick: (View, String, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val image = binding.image
    private val title = binding.title
    private val author = binding.author
    private val favorite = binding.favorite
    private val resources = itemView.resources

    private lateinit var book: UserBestseller

    init {
        favorite.setOnClickListener {
            onFavoriteClick(it, book.id, book.isFavorite)
        }
        binding.root.setOnClickListener {
            onBookClick(it, book)
        }
    }

    fun bind(book: UserBestseller) {
        this.book = book
        if (book.image.isNotEmpty()) {
            provideGlide(this.image, book.image)
        }
        title.text = book.title
        author.text = book.author
        favorite.foreground = if (book.isFavorite) {
            ResourcesCompat.getDrawable(resources, R.drawable.fav_filled_icon, null)
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.fav_outline_icon, null)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onBookClick: (View, UserBestseller) -> Unit,
            onFavoriteClick: (View, String, Boolean) -> Unit
        ): BestsellerViewHolder {
            val itemBinding = BookEntryBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
            return BestsellerViewHolder(itemBinding, onBookClick, onFavoriteClick)
        }
    }
}