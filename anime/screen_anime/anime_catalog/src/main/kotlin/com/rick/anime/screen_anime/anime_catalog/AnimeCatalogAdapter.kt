package com.rick.anime.screen_anime.anime_catalog

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data.model_anime.UserAnime

class AnimeCatalogAdapter(
    private val onItemClick: (View, Int) -> Unit,
    private val onAnimeFavClick: (Int, Boolean) -> Unit,
) : PagingDataAdapter<UserAnime, AnimeViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = getItem(position)
        if (anime != null) {
            holder.bind(anime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        return AnimeViewHolder.create(parent, onItemClick, onAnimeFavClick)
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<UserAnime>() {
            override fun areItemsTheSame(oldItem: UserAnime, newItem: UserAnime): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserAnime, newItem: UserAnime): Boolean {
                return oldItem == newItem
            }
        }
    }
}