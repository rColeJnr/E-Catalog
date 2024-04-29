package com.rick.anime.screen_anime.manga_catalog

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data.model_anime.UserManga

class MangaCatalogAdapter(
    private val onItemClick: (View, Int) -> Unit,
    private val onMangaFavClick: (Int, Boolean) -> Unit,
) : PagingDataAdapter<UserManga, MangaViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        val anime = getItem(position)
        if (anime != null) {
            holder.bind(anime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        return MangaViewHolder.create(parent, onItemClick, onMangaFavClick)
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<UserManga>() {
            override fun areItemsTheSame(oldItem: UserManga, newItem: UserManga): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserManga, newItem: UserManga): Boolean {
                return oldItem == newItem
            }
        }
    }
}