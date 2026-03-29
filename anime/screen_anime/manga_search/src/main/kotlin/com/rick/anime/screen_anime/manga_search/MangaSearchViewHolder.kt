package com.rick.anime.screen_anime.manga_search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.anime.screen_anime.manga_search.databinding.AnimeScreenAnimeMangaSearchMangaEntryBinding
import com.rick.data.model_anime.UserManga
import com.rick.data.ui_components.common.provideGlide

class MangaSearchViewHolder(
    binding: AnimeScreenAnimeMangaSearchMangaEntryBinding,
    private val onItemClick: (View, Int) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val favorite = binding.favorite
    private val resources = itemView.resources

    init {
        binding.root.setOnClickListener {
            onItemClick(it, manga.id)
        }
        favorite.setOnClickListener {
            onFavClick(manga.id, manga.isFavorite)
        }
    }

    private lateinit var manga: UserManga

    fun bind(manga: UserManga) {
        this.manga = manga
        this.title.text = manga.title
        provideGlide(this.image, manga.images)
        this.synopsis.text = manga.synopsis
        val icon = if (manga.isFavorite) {
            com.rick.data.ui_design.R.drawable.data_ui_design_favorite_outlined
        } else {
            com.rick.data.ui_design.R.drawable.data_ui_design_favorite_filled
        }
        favorite.setImageResource(icon)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (View, Int) -> Unit,
            onFavClick: (Int, Boolean) -> Unit,
        ): MangaSearchViewHolder {
            val itemBinding =
                AnimeScreenAnimeMangaSearchMangaEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return MangaSearchViewHolder(itemBinding, onItemClick, onFavClick)
        }
    }

}