package com.rick.screen_anime

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data_anime.model_jikan.Jikan

class AnimeCatalogAdapter(
    private val onItemClick: (view: View, anime: Jikan) -> Unit
) : PagingDataAdapter<Jikan, AnimeViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = getItem(position)
        if (anime != null) {
            holder.bind(anime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        return AnimeViewHolder.create(parent, onItemClick)
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Jikan>() {
            override fun areItemsTheSame(oldItem: Jikan, newItem: Jikan): Boolean {
                return oldItem.malId == newItem.malId
            }

            override fun areContentsTheSame(oldItem: Jikan, newItem: Jikan): Boolean {
                return oldItem == newItem
            }
        }
    }
}