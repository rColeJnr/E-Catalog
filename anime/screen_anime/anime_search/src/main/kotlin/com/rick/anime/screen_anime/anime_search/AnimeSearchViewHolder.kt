package com.rick.anime.screen_anime.anime_search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.anime.screen_anime.anime_search.databinding.AnimeScreenAnimeAnimeSearchAnimeEntryBinding
import com.rick.data.model_anime.UserAnime
import com.rick.data.ui_components.common.provideGlide

class AnimeSearchViewHolder(
    binding: AnimeScreenAnimeAnimeSearchAnimeEntryBinding,
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
        jikan.background.let { provideGlide(this.image, it) }
        this.synopsis.text = jikan.synopsis
        favorite.foreground = if (jikan.isFavorite) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.anime_screen_anime_anime_search_star_filled,
                null
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.anime_screen_anime_anime_search_star_outlined,
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
        ): AnimeSearchViewHolder {
            val itemBinding =
                AnimeScreenAnimeAnimeSearchAnimeEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return AnimeSearchViewHolder(itemBinding, onItemClick, onFavClick)
        }
    }

}