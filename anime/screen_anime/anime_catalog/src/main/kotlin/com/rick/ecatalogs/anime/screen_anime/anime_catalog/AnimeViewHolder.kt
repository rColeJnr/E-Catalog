package com.rick.ecatalogs.anime.screen_anime.anime_catalog

import com.rick.ecatalogs.data.ui_components.common.TranslationToggle
import com.rick.ecatalogs.data.translation.TextTranslator
import android.view.LayoutInflater
import com.rick.ecatalogs.data.ui_design.POSTER_KIND_ANIME
import com.rick.ecatalogs.data.ui_design.posterTransitionName
import androidx.core.view.ViewCompat
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.anime.screen_anime.anime_catalog.databinding.AnimeScreenAnimeAnimeCatalogAnimeEntryBinding
import com.rick.ecatalogs.anime.data_anime.model.UserAnime
import com.rick.ecatalogs.data.ui_components.common.provideGlide

class AnimeViewHolder(
    binding: AnimeScreenAnimeAnimeCatalogAnimeEntryBinding,
    private val onItemClick: (View, Int) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit,
    translator: TextTranslator,
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val favorite = binding.favorite
    private val translation = TranslationToggle(binding.translate, synopsis, translator)
    private val resources = itemView.resources

    private lateinit var anime: UserAnime

    init {
        // The poster card is clickable itself and takes its taps: listen on it too, not only on
        // the row (which only gets taps outside the card)
        val open = View.OnClickListener { onItemClick(image, anime.id) }
        binding.movieEntryCardView.setOnClickListener(open)
        binding.root.setOnClickListener(open)
        favorite.setOnClickListener {
            onFavClick(it, anime.id, anime.isFavorite)
        }
    }

    fun bind(anime: UserAnime) {
        this.anime = anime
        ViewCompat.setTransitionName(image, posterTransitionName(POSTER_KIND_ANIME, anime.id))
        this.title.text = anime.title
        if (anime.images.isNotEmpty()) {
            provideGlide(this.image, anime.images)
        }
        translation.bind(anime.synopsis)
        favorite.setImageResource(
            if (anime.isFavorite) {
                com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_filled
            } else {
                com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_outlined
            }
        )
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (View, Int) -> Unit,
            onFavClick: (View, Int, Boolean) -> Unit,
            translator: TextTranslator,
        ): AnimeViewHolder {
            val itemBinding = AnimeScreenAnimeAnimeCatalogAnimeEntryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return AnimeViewHolder(itemBinding, onItemClick, onFavClick, translator)
        }
    }
}