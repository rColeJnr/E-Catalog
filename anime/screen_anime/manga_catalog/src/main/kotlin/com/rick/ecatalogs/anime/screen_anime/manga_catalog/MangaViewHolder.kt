package com.rick.ecatalogs.anime.screen_anime.manga_catalog

import com.rick.ecatalogs.data.ui_components.common.TranslationToggle
import com.rick.ecatalogs.data.translation.TextTranslator
import android.view.LayoutInflater
import com.rick.ecatalogs.data.ui_design.POSTER_KIND_MANGA
import com.rick.ecatalogs.data.ui_design.posterTransitionName
import androidx.core.view.ViewCompat
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.anime.screen_anime.manga_catalog.databinding.AnimeScreenAnimeMangaCatalogMangaEntryBinding
import com.rick.ecatalogs.anime.data_anime.model.UserManga
import com.rick.ecatalogs.data.ui_components.common.provideGlide

class MangaViewHolder(
    binding: AnimeScreenAnimeMangaCatalogMangaEntryBinding,
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

    init {
        // The poster card is clickable itself and takes its taps: listen on it too, not only on
        // the row (which only gets taps outside the card)
        val open = View.OnClickListener { onItemClick(image, manga.id) }
        binding.movieEntryCardView.setOnClickListener(open)
        binding.root.setOnClickListener(open)
        favorite.setOnClickListener {
            onFavClick(it, manga.id, manga.isFavorite)
        }
    }

    private lateinit var manga: UserManga

    fun bind(manga: UserManga) {
        this.manga = manga
        ViewCompat.setTransitionName(image, posterTransitionName(POSTER_KIND_MANGA, manga.id))
        this.title.text = manga.title
        if (manga.images.isNotEmpty()) {
            provideGlide(this.image, manga.images)
        }
        translation.bind(manga.synopsis)
        favorite.setImageResource(
            if (manga.isFavorite) {
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
        ): MangaViewHolder {
            val itemBinding =
                AnimeScreenAnimeMangaCatalogMangaEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return MangaViewHolder(itemBinding, onItemClick, onFavClick, translator)
        }
    }

}