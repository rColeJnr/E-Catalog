package com.rick.screen_anime.manga_screen

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data_anime.model_manga.Manga

class MangaCatalogAdapter(
    private val onItemClick: (view: View, manga: Manga) -> Unit
): PagingDataAdapter<Manga, MangaViewHolder>(DIFF_UTIL) {

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        TODO("Not yet implemented")
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Manga>(){
            override fun areItemsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                return oldItem.malId == newItem.malId
            }

            override fun areContentsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                return oldItem == newItem
            }
        }
    }
}