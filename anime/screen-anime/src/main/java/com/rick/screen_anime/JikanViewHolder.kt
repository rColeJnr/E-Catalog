package com.rick.screen_anime

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rick.data_anime.model_jikan.Jikan
import com.rick.screen_anime.databinding.JikanEntryBinding

class JikanViewHolder(
    binding: JikanEntryBinding,
    private val onItemClick: (view: View, anime: Jikan) -> Unit
) : RecyclerView.ViewHolder(binding.root),
    View.OnClickListener {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val pgRating = binding.pgRating

    init {
        binding.root.setOnClickListener(this)
    }

    private lateinit var jikan: Jikan

    fun bind(anime: Jikan) {
        this.jikan = anime
        val resources = itemView.resources
        this.title.text = anime.title
        anime.images.jpg.imageUrl.let { provideGlide(this.image, it) }
        this.synopsis.text = anime.synopsis
        this.pgRating.text = if (jikan.type == "Anime") resources.getString(R.string.pg_rating, anime.rating)
        else resources.getString(R.string.authors, getListAsString(jikan.authors))
    }
    
    override fun onClick(view: View) {
        onItemClick(view, jikan)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (view: View, anime: Jikan) -> Unit
        ): JikanViewHolder {
            val itemBinding =
                JikanEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return JikanViewHolder(itemBinding, onItemClick)
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
