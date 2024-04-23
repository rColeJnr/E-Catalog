package com.rick.screen_anime.search_screen

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_anime.UserAnime
import com.rick.screen_anime.anime_screen.AnimeViewHolder

class SearchJikanAdapter(
    private val onItemClick: (view: View, jikan: UserAnime) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit,
) : RecyclerView.Adapter<AnimeViewHolder>() {

    val differ = AsyncListDiffer(this, DIFF_UTIL)

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val jikan = differ.currentList[position]
        holder.bind(jikan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        return AnimeViewHolder.create(
            parent,
            onItemClick,
            onFavClick,
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
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