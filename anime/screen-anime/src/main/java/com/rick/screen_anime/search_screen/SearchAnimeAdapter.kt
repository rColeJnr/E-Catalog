package com.rick.screen_anime.search_screen

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_anime.model_jikan.Jikan

class SearchAnimeAdapter(
    private val onItemClick: (view: View, anime: Jikan) -> Unit
): RecyclerView.Adapter<SearchAnimeViewHolder>() {

    val differ = AsyncListDiffer(this, DIFF_UTIL)

    override fun onBindViewHolder(holder: SearchAnimeViewHolder, position: Int) {
        val anime = differ.currentList[position]
        holder.bind(anime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAnimeViewHolder {
        return SearchAnimeViewHolder.create(parent, onItemClick)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
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