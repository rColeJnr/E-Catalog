package com.rick.screen_anime.manga_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rick.data_anime.model_manga.Manga
import com.rick.screen_anime.R
import com.rick.screen_anime.databinding.AnimeEntryBinding

class MangaViewHolder(
    binding: AnimeEntryBinding,
    private val onItemClick: (view: View, manga: Manga) -> Unit
) : RecyclerView.ViewHolder(binding.root),
    View.OnClickListener {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val pgRating = binding.pgRating

    private lateinit var manga: Manga

    fun bind(manga: Manga) {
        this.manga = manga
        val resources = itemView.resources
        this.title.text = manga.title
        manga.imagesDto.jpg.imageUrl.let { provideGlide(this.image, it) }
        this.synopsis.text = manga.synopsis
        this.pgRating.text = resources.getString(R.string.authors, getListAsString(manga.authors))
    }


    override fun onClick(view: View) {
        onItemClick(view, manga)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (view: View, manga: Manga) -> Unit
        ): MangaViewHolder {
            val itemBinding =
                AnimeEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MangaViewHolder(itemBinding, onItemClick)
        }
    }

}

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