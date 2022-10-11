package com.rick.screen_book

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rick.data_book.model.Book
import com.rick.screen_book.databinding.BookEntryBinding

class BookCatalogViewHolder(
    binding: BookEntryBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val title = binding.bookTitle
    private val image = binding.image
    private val authors = binding.authors
    private val translators = binding.translators
    private val topics = binding.topics
    private val bookshelves = binding.bookshelf
    private val languages = binding.languages
    private val downloaded = binding.downloaded

    private lateinit var book: Book

    fun bind(book: Book) {
        this.book = book
        val resources = itemView.resources
        book.formats.image.let { provideGlide(this.image, it) }
        this.title.text = book.title
        this.authors.text = resources.getString(R.string.authors, getListAsString(book.authors))
        this.translators.text = resources.getString(R.string.translators, getListAsString(book.translators))
        this.topics.text = resources.getString(R.string.topics, getListAsString(book.subjects))
        this.bookshelves.text = resources.getString(R.string.bookshelf, getListAsString(book.bookshelves))
        this.languages.text = resources.getString(R.string.languages, getListAsString(book.languages))
        this.downloaded.text = resources.getString(R.string.downloaded, book.downloads.toString())
    }

    private fun getListAsString(list: List<Any>): String {
        val sb = StringBuilder()

        list.forEach { sb.append("$it\n") }
        return sb.toString()
    }

    companion object {
        fun create(parent: ViewGroup): BookCatalogViewHolder {
            val itemBinding = BookEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return BookCatalogViewHolder(itemBinding)
        }
    }

}

fun provideGlide(view: ImageView, src: String) {
    val circularProgressDrawable = CircularProgressDrawable(view.context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }
    Glide.with(view)
        .load(src)
        .apply(
            RequestOptions().placeholder(circularProgressDrawable)
        )
        .into(view)
}