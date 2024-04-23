package com.rick.screen_book.gutenberg_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rick.data.model_book.UserGutenberg
import com.rick.data.model_book.gutenberg.Formats
import com.rick.screen_book.R
import com.rick.screen_book.databinding.BookEntryBinding

class BookCatalogViewHolder(
    binding: BookEntryBinding,
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
            ResourcesCompat.getDrawable(resources, R.drawable.fav_filled_icon, null)
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.fav_outline_icon, null)
        }
        this.title.text = book.title
        this.authors.text = resources.getString(R.string.authors, getListAsString(book.authors))

    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (view: View, formats: Formats) -> Unit,
            onFavClick: (Int, Boolean) -> Unit
        ):
                BookCatalogViewHolder {
            val itemBinding = BookEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return BookCatalogViewHolder(itemBinding, onItemClick, onFavClick)
        }
    }

}

// TODO Clean this up
fun getListAsString(list: List<Any>): String {
    val sb = StringBuilder()

    list.forEach { sb.append("$it ") }
    return sb.toString()
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