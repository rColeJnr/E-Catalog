package com.rick.screen_anime.search_screen

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_anime.favorite.JikanFavorite
import com.rick.data_anime.model_jikan.Jikan
import com.rick.screen_anime.JikanViewHolder

class SearchJikanAdapter(
    private val onItemClick: (view: View, jikan: Jikan) -> Unit,
    private val onFavClick: (view: View, favorite: JikanFavorite) -> Unit
): RecyclerView.Adapter<JikanViewHolder>() {

    val differ = AsyncListDiffer(this, DIFF_UTIL)

    override fun onBindViewHolder(holder: JikanViewHolder, position: Int) {
        val jikan = differ.currentList[position]
        holder.bind(jikan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JikanViewHolder {
        return JikanViewHolder.create(parent, onItemClick, onFavClick)
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