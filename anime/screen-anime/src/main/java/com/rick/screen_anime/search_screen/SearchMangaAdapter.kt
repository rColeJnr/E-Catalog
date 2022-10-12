package com.rick.screen_anime.search_screen

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_anime.model_manga.Manga

class SearchMangaAdapter(
    private val onItemClick: (view: View, manga: Manga) -> Unit,
) : RecyclerView.Adapter<SearchMangaViewHolder>() {

    val differ = AsyncListDiffer(this, DIFF_UTIL)

    override fun onBindViewHolder(holder: SearchMangaViewHolder, position: Int) {
        val manga = differ.currentList.get(position)
        holder.bind(manga)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMangaViewHolder {
        return SearchMangaViewHolder.create(parent, onItemClick)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Manga>() {
            override fun areItemsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                return oldItem.malId == newItem.malId
            }

            override fun areContentsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                return oldItem == newItem
            }
        }
    }
}