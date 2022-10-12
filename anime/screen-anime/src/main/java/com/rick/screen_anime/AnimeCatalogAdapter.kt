package com.rick.screen_anime

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data_anime.model_anime.Anime
import com.rick.data_anime.model_manga.Manga
import com.rick.screen_anime.manga_screen.MangaViewHolder

class AnimeCatalogAdapter(
    private val onItemClick: (view: View, anime: Anime) -> Unit
) : PagingDataAdapter<Anime, AnimeViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        val anime = getItem(position)
        if (anime != null) {
            holder.bind(anime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        return MangaViewHolder.create(parent, onItemClick)
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Anime>() {
            override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean {
                return oldItem.malId == newItem.malId
            }

            override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean {
                return oldItem == newItem
            }
        }
    }
}