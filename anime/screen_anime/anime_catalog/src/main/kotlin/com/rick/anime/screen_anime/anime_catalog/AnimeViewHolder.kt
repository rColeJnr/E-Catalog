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
    private val onFavClick: (Int, Boolean) -> Unit,
    private val onTranslationClick: (View, List<String>) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val favorite = binding.favorite
    private val showTranslation = binding.translate
    private val resources = itemView.resources

    private lateinit var anime: UserAnime

    init {
        binding.root.setOnClickListener {
            onItemClick(it, anime.id)
        }
        favorite.setOnClickListener {
            onFavClick(anime.id, anime.isFavorite)
        }
        showTranslation.setOnClickListener {
            onTranslationClick(synopsis, listOf(anime.synopsis))
        }
    }

    fun bind(anime: UserAnime) {
        this.anime = anime
        this.title.text = anime.title
        if (anime.images.isNotEmpty()) {
            provideGlide(this.image, anime.images)
        }
        this.synopsis.text = this.anime.synopsis
        favorite.setImageResource(
            if (anime.isFavorite) {
                R.drawable.anime_screen_anime_anime_catalog_star_filled
            } else {
                R.drawable.anime_screen_anime_anime_catalog_star_outlined
            }
        )
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (View, Int) -> Unit,
            onFavClick: (Int, Boolean) -> Unit,
            onTranslateClick: (View, List<String>) -> Unit
        ): AnimeViewHolder {
            val itemBinding = AnimeScreenAnimeAnimeCatalogAnimeEntryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return AnimeViewHolder(itemBinding, onItemClick, onFavClick, onTranslateClick)
        }
    }
}