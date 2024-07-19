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
    private val favorite = binding.favButton
    private val showTranslation = binding.translate
    private val showOriginal = binding.showOriginal
    private val resources = itemView.resources

    init {
        binding.root.setOnClickListener {
            onItemClick(it, anime.id)
        }
        favorite.setOnClickListener {
            onFavClick(anime.id, anime.isFavorite)
        }
        showTranslation.setOnClickListener {
            onTranslationClick(synopsis, listOf(anime.synopsis))
            showOriginal.visibility = View.VISIBLE
            it.visibility = View.GONE
        }
        showOriginal.setOnClickListener {
            synopsis.text = anime.synopsis
            it.visibility = View.GONE
            showTranslation.visibility = View.VISIBLE
        }
    }

    private lateinit var anime: UserAnime

    fun bind(anime: UserAnime) {
        this.anime = anime
        this.title.text = anime.title
        if (anime.images.isNotEmpty()) {
            provideGlide(this.image, anime.images)
        }
        this.synopsis.text = this.anime.synopsis
        favorite.foreground = if (anime.isFavorite) {
            ResourcesCompat.getDrawable(
                resources, R.drawable.anime_screen_anime_anime_catalog_star_filled, null
            )
        } else {
            ResourcesCompat.getDrawable(
                resources, R.drawable.anime_screen_anime_anime_catalog_star_outlined, null
            )
        }
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