package com.rick.book.screen_book.gutenberg_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.book.screen_book.gutenberg_catalog.databinding.BookScreenBookGutenbergCatalogGutenbergEntryBinding
import com.rick.data.model_book.gutenberg.Formats
import com.rick.data.model_book.gutenberg.UserGutenberg
import com.rick.data.ui_components.common.getListAsString
import com.rick.data.ui_components.common.provideGlide

class GutenbergCatalogViewHolder(
    binding: BookScreenBookGutenbergCatalogGutenbergEntryBinding,
    private val onItemClick: (view: View, formats: Formats) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private val title = binding.title
    private val image = binding.image
    private val authors = binding.author
    private val favorite = binding.favorite
    private val resources = itemView.resources

    private lateinit var book: UserGutenberg

    init {
        // ain't no way this gon work.
        // this may  work now
        binding.root.setOnClickListener {
            onItemClick(it, book.formats)
        }
        favorite.setOnClickListener {
            onFavClick(book.id, book.isFavorite)
        }
    }

//    override fun onClick(view: View) {
//        onItemClick(view, book.formats)
//        onFavClick(view, Favorite(0,"titie", "ricardo e simara"))
//    }

    fun bind(book: UserGutenberg) {
        this.book = book
        book.formats.imageJpeg?.let { provideGlide(this.image, it) }
        favorite.foreground = if (book.isFavorite) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.book_screen_book_gutenberg_catalog_ic_fav_filled,
                null
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.book_screen_book_gutenberg_catalog_ic_fav_outlined,
                null
            )
        }
        this.title.text = book.title
        this.authors.text = resources.getString(
            R.string.book_screen_book_gutenberg_catalog_authors,
            getListAsString(book.authors)
        )

    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (view: View, formats: Formats) -> Unit,
            onFavClick: (Int, Boolean) -> Unit
        ):
                GutenbergCatalogViewHolder {
            val itemBinding = BookScreenBookGutenbergCatalogGutenbergEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return GutenbergCatalogViewHolder(itemBinding, onItemClick, onFavClick)
        }
    }

}