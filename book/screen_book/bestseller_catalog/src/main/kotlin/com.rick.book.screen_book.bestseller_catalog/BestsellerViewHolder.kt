package com.rick.book.screen_book.bestseller_catalog

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.res.stringResource
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.book.screen_book.bestseller_catalog.databinding.BookScreenBookBestsellerCatalogBookEntryBinding
import com.rick.data.model_book.bestseller.UserBestseller
import com.rick.data.ui_components.common.provideGlide
import java.util.Locale
import java.util.Locale.getDefault

class BestsellerViewHolder(
    binding: BookScreenBookBestsellerCatalogBookEntryBinding,
    private val onBookClick: (UserBestseller) -> Unit,
    private val onFavoriteClick: (View, String, Boolean) -> Unit,
    private val onTranslationClick: (UserBestseller, List<String>) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val image = binding.image
    private val rank = binding.rank
    private val favorite = binding.favorite
    private val title = binding.title
    private val author = binding.author
    private val resources = itemView.resources

    private lateinit var book: UserBestseller

    init {
        favorite.setOnClickListener {
            onFavoriteClick(it, book.id, book.isFavorite)
        }
        binding.root.setOnClickListener {
            onBookClick(book)
        }
    }

    fun bind(book: UserBestseller) {
        this.book = book
        if (book.image.isNotEmpty()) {
            provideGlide(this.image, book.image)
        }
        rank.text =
            resources.getString(R.string.book_screen_book_bestseller_catalog_rank, book.rank)
        title.text = run {
            book.title.lowercase().replaceFirstChar { it.titlecase(getDefault()) }
        }
        author.text = book.author
        favorite.setImageResource(
            if (book.isFavorite) {
                R.drawable.book_screen_book_bestseller_catalog_ic_fav_filled
            } else {
                R.drawable.book_screen_book_bestseller_catalog_ic_fav_outlined
            }
        )
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onBookClick: (UserBestseller) -> Unit,
            onFavoriteClick: (View, String, Boolean) -> Unit,
            onTranslationClick: (UserBestseller, List<String>) -> Unit
        ): BestsellerViewHolder {
            val itemBinding = BookScreenBookBestsellerCatalogBookEntryBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
            return BestsellerViewHolder(
                itemBinding,
                onBookClick,
                onFavoriteClick,
                onTranslationClick
            )
        }
    }
}