package com.rick.screen_book.bestseller_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_book.nytimes.model.NYBook
import com.rick.screen_book.databinding.BookEntryBinding
import com.rick.screen_book.provideGlide

class BestsellerViewHolder(
    binding: BookEntryBinding,
    private val onBookClick: (book: NYBook) -> Unit,
    private val onFavoriteClick: (book: NYBook) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val image = binding.image
    private val title = binding.title
    private val author = binding.author
    private val favorite = binding.favorite

    private lateinit var book: NYBook

    init {
        binding.favorite.setOnClickListener {
            onFavoriteClick(book)
        }
        binding.root.setOnClickListener {
            onBookClick(book)
        }
    }

    fun bind(book: NYBook) {
        this.book = book
        if (book.bookImage.isNotEmpty()) {
            provideGlide(this.image, book.bookImage)
        }
        title.text = book.title
        author.text = book.author
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onBookClick: (book: NYBook) -> Unit,
            onFavoriteClick: (book: NYBook) -> Unit
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