package com.rick.anime.screen_anime.manga_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.anime.screen_anime.manga_catalog.databinding.AnimeScreenAnimeMangaCatalogMangaEntryBinding
import com.rick.data.model_anime.UserManga
import com.rick.data.ui_components.common.provideGlide

class MangaViewHolder(
    binding: AnimeScreenAnimeMangaCatalogMangaEntryBinding,
    private val onItemClick: (View, Int) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (View, List<String>) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val favorite = binding.favorite
    private val showTranslation = binding.translate
    private val resources = itemView.resources

    init {
        binding.root.setOnClickListener {
            onItemClick(it, manga.id)
        }
        favorite.setOnClickListener {
            onFavClick(it, manga.id, manga.isFavorite)
        }
        showTranslation.setOnClickListener {
            onTranslationClick(synopsis, listOf(manga.synopsis))
        }
    }

    private lateinit var manga: UserManga

    fun bind(manga: UserManga) {
        this.manga = manga
        this.title.text = manga.title
        if (manga.images.isNotEmpty()) {
            provideGlide(this.image, manga.images)
        }
        this.synopsis.text = manga.synopsis
        favorite.setImageResource(
            if (manga.isFavorite) {
                R.drawable.anime_screen_anime_manga_catalog_star_filled
            } else {
                R.drawable.anime_screen_anime_manga_catalog_star_outlined
            }
        )
    }

//    override fun onClick(view: View) {
//        onItemClick(view, jikan)
//    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (View, Int) -> Unit,
            onFavClick: (View, Int, Boolean) -> Unit,
            onTranslationClick: (View, List<String>) -> Unit
        ): MangaViewHolder {
            val itemBinding =
                AnimeScreenAnimeMangaCatalogMangaEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return MangaViewHolder(itemBinding, onItemClick, onFavClick, onTranslationClick)
        }
    }

}