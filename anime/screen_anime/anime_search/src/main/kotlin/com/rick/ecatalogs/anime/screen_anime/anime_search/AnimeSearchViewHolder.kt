package com.rick.ecatalogs.anime.screen_anime.anime_search

import com.rick.ecatalogs.data.ui_components.common.TranslationToggle
import com.rick.ecatalogs.data.translation.TextTranslator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.anime.screen_anime.anime_search.databinding.AnimeScreenAnimeAnimeSearchAnimeEntryBinding
import com.rick.ecatalogs.anime.data_anime.model.UserAnime
import com.rick.ecatalogs.data.ui_components.common.provideGlide

class AnimeSearchViewHolder(
    binding: AnimeScreenAnimeAnimeSearchAnimeEntryBinding,
    private val onItemClick: (View, Int) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit,
    translator: TextTranslator,
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val translation = TranslationToggle(binding.translate, synopsis, translator)
    private val favorite = binding.favorite
    private val resources = itemView.resources

    init {
        binding.root.setOnClickListener {
            onItemClick(it, jikan.id)
        }
        favorite.setOnClickListener {
            onFavClick(jikan.id, jikan.isFavorite)
        }
    }

    private lateinit var jikan: UserAnime

    fun bind(jikan: UserAnime) {
        this.jikan = jikan
        this.title.text = jikan.title
        if (jikan.images.isNotEmpty()) provideGlide(this.image, jikan.images)
        translation.bind(jikan.synopsis)
        favorite.foreground = if (jikan.isFavorite) {
            ResourcesCompat.getDrawable(
                resources,
                com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_filled,
                null
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_outlined,
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
            translator: TextTranslator,
        ): AnimeSearchViewHolder {
            val itemBinding =
                AnimeScreenAnimeAnimeSearchAnimeEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return AnimeSearchViewHolder(itemBinding, onItemClick, onFavClick, translator)
        }
    }

}