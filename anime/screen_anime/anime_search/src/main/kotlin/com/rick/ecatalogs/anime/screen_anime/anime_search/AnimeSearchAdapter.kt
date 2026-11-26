package com.rick.ecatalogs.anime.screen_anime.anime_search

import com.rick.ecatalogs.data.translation.TextTranslator
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.anime.data_anime.model.UserAnime

class AnimeSearchAdapter(
    private val onItemClick: (View, Int) -> Unit,
    private val onFavClick: (Int, Boolean) -> Unit,
    private val translator: TextTranslator,
) : RecyclerView.Adapter<AnimeSearchViewHolder>() {

    val differ = AsyncListDiffer(this, DIFF_UTIL)

    override fun onBindViewHolder(holder: AnimeSearchViewHolder, position: Int) {
        val jikan = differ.currentList[position]
        holder.bind(jikan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeSearchViewHolder {
        return AnimeSearchViewHolder.create(
            parent,
            onItemClick,
            onFavClick,
            translator,
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