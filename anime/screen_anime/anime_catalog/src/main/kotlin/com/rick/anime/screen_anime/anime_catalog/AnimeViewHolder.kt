package com.rick.anime.screen_anime.anime_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.anime.screen_anime.anime_catalog.databinding.AnimeScreenAnimeAnimeCatalogAnimeEntryBinding
import com.rick.data.model_anime.UserAnime
import com.rick.data.ui_components.common.provideGlide

class AnimeViewHolder(
    binding: AnimeScreenAnimeAnimeCatalogAnimeEntryBinding,
    private val onItemClick: (View, Int) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val favorite = binding.favButton
    private val resources = itemView.resources

    init {
        binding.root.setOnClickListener {
            onItemClick(it, anime.id)
        }
        favorite.setOnClickListener {
            onFavClick(anime.id, anime.isFavorite)
        }
    }

    private lateinit var anime: UserAnime

    fun bind(anime: UserAnime) {
        this.anime = anime
        this.title.text = anime.title
        if (anime.images.isNotEmpty()) {
            provideGlide(this.image, anime.images)
        }
        this.synopsis.text = anime.synopsis
        favorite.foreground = if (anime.isFavorite) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.anime_screen_anime_anime_catalog_ic_filled_favorite,
                null
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.anime_screen_anime_anime_catalog_ic_outlined_favorite,
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
            onItemClick: (View, Int) -> Unit,
            onFavClick: (Int, Boolean) -> Unit,
        ): AnimeViewHolder {
            val itemBinding =
                AnimeScreenAnimeAnimeCatalogAnimeEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return AnimeViewHolder(itemBinding, onItemClick, onFavClick)
        }
    }

}