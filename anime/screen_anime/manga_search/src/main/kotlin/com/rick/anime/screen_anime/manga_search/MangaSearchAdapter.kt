package com.rick.anime.screen_anime.manga_search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_anime.UserManga

class MangaSearchAdapter(
    private val onItemClick: (View, Int) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit,
) : RecyclerView.Adapter<MangaSearchViewHolder>() {

    val differ = AsyncListDiffer(this, DIFF_UTIL)

    override fun onBindViewHolder(holder: MangaSearchViewHolder, position: Int) {
        val jikan = differ.currentList[position]
        holder.bind(jikan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaSearchViewHolder {
        return MangaSearchViewHolder.create(
            parent,
            onItemClick,
            onFavClick,
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
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