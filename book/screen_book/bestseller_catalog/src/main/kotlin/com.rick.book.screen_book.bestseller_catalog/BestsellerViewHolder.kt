package com.rick.book.screen_book.bestseller_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.book.screen_book.bestseller_catalog.databinding.BookScreenBookBestsellerCatalogBookEntryBinding
import com.rick.data.model_book.bestseller.UserBestseller
import com.rick.data.ui_components.common.provideGlide

class BestsellerViewHolder(
    binding: BookScreenBookBestsellerCatalogBookEntryBinding,
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
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.book_screen_book_bestseller_catalog_ic_fav_filled,
                null
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.book_screen_book_bestseller_catalog_ic_fav_outlined,
                null
            )
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onBookClick: (View, UserBestseller) -> Unit,
            onFavoriteClick: (View, String, Boolean) -> Unit
        ): BestsellerViewHolder {
            val itemBinding = BookScreenBookBestsellerCatalogBookEntryBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
            return BestsellerViewHolder(itemBinding, onBookClick, onFavoriteClick)
        }
    }
}