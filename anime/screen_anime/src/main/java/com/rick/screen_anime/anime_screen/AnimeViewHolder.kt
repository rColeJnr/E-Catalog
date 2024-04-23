package com.rick.screen_anime.anime_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_anime.UserAnime
import com.rick.screen_anime.R
import com.rick.screen_anime.databinding.AnimeScreenAnimeJikanEntryBinding
import com.rick.screen_anime.provideGlide

class AnimeViewHolder(
    binding: AnimeScreenAnimeJikanEntryBinding,
    private val onItemClick: (View, UserAnime) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val favorite = binding.favButton
    private val resources = itemView.resources

    init {
        binding.root.setOnClickListener {
            onItemClick(it, jikan)
        }
        favorite.setOnClickListener {
            onFavClick(jikan.id, jikan.isFavorite)
        }
    }

    private lateinit var jikan: UserAnime

    fun bind(jikan: UserAnime) {
        this.jikan = jikan
        this.title.text = jikan.title
        jikan.background.let { provideGlide(this.image, it) }
        this.synopsis.text = jikan.synopsis
        favorite.foreground = if (jikan.isFavorite) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.anime_screen_anime_fav_filled_icon,
                null
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.anime_screen_anime_fav_outline_icon,
                null
            )
        }
    }

//    override fun onClick(view: View) {
//        onItemClick(view, jikan)
//    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (view: View, jikan: UserAnime) -> Unit,
            onFavClick: (Int, Boolean) -> Unit,
        ): AnimeViewHolder {
            val itemBinding =
                AnimeScreenAnimeJikanEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return AnimeViewHolder(itemBinding, onItemClick, onFavClick)
        }
    }

}