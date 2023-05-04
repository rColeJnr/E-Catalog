package com.rick.screen_anime

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rick.data_anime.favorite.JikanFavorite
import com.rick.data_anime.model_jikan.Jikan
import com.rick.screen_anime.databinding.JikanEntryBinding

class JikanViewHolder(
    binding: JikanEntryBinding,
    private val onItemClick: (view: View, jikan: Jikan) -> Unit,
    private val onFavClick: (view: View, favorite: JikanFavorite) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val pgRating = binding.pgRating

    init {
        binding.root.setOnClickListener{
            onItemClick(it, jikan)
        }
        binding.favButton.setOnClickListener {
            onFavClick(it, jikan.toFavorite())
        }
    }

    private lateinit var jikan: Jikan

    fun bind(jikan: Jikan) {
        this.jikan = jikan
        val resources = itemView.resources
        this.title.text = jikan.title
        jikan.images.jpg.imageUrl.let { provideGlide(this.image, it) }
        this.synopsis.text = jikan.synopsis
        this.pgRating.text = if (this.jikan.type == "TV") resources.getString(R.string.pg_rating, jikan.rating)
        else resources.getString(R.string.authors, getListAsString(jikan.authors!!.map { it!! }))
    }
    
//    override fun onClick(view: View) {
//        onItemClick(view, jikan)
//    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (view: View, jikan: Jikan) -> Unit,
            onFavClick: (view: View, favorite: JikanFavorite) -> Unit
        ): JikanViewHolder {
            val itemBinding =
                JikanEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return JikanViewHolder(itemBinding, onItemClick, onFavClick)
        }
    }

}
//Dude, we really just have to sit and clean this code.
// Then start writing tests, we already more than enough features.
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
