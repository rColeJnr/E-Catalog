package com.rick.screen_anime.manga_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_anime.UserManga
import com.rick.screen_anime.R
import com.rick.screen_anime.databinding.AnimeScreenAnimeJikanEntryBinding
import com.rick.screen_anime.provideGlide

class MangaViewHolder(
    binding: AnimeScreenAnimeJikanEntryBinding,
    private val onItemClick: (View, UserManga) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val favorite = binding.favButton
    private val resources = itemView.resources

    init {
        binding.root.setOnClickListener {
            onItemClick(it, manga)
        }
        favorite.setOnClickListener {
            onFavClick(manga.id, manga.isFavorite)
        }
    }

    private lateinit var manga: UserManga

    fun bind(manga: UserManga) {
        this.manga = manga
        this.title.text = manga.title
        manga.background.let { provideGlide(this.image, it) }
        this.synopsis.text = manga.synopsis
        favorite.foreground = if (manga.isFavorite) {
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
            onItemClick: (View, UserManga) -> Unit,
            onFavClick: (Int, Boolean) -> Unit,
        ): MangaViewHolder {
            val itemBinding =
                AnimeScreenAnimeJikanEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return MangaViewHolder(itemBinding, onItemClick, onFavClick)
        }
    }

}