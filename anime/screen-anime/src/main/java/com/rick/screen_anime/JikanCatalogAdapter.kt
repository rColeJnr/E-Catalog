package com.rick.screen_anime

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data_anime.model_jikan.Jikan

class JikanCatalogAdapter(
    private val onItemClick: (view: View, jikan: Jikan) -> Unit
) : PagingDataAdapter<Jikan, JikanViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: JikanViewHolder, position: Int) {
        val anime = getItem(position)
        if (anime != null) {
            holder.bind(anime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JikanViewHolder {
        return JikanViewHolder.create(parent, onItemClick)
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