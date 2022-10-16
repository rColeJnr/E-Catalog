package com.rick.screen_anime.manga_screen

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data_anime.model_jikan.Jikan
import com.rick.data_anime.model_manga.Manga

class MangaCatalogAdapter(
    private val onItemClick: (view: View, manga: Jikan) -> Unit
) : PagingDataAdapter<Jikan, MangaViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        val manga = getItem(position)
        if (manga != null) {
            holder.bind(manga)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        return MangaViewHolder.create(parent, onItemClick)
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