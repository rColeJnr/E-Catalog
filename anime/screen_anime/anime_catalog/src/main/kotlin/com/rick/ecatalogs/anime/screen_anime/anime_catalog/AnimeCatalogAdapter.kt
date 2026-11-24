package com.rick.ecatalogs.anime.screen_anime.anime_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.anime.screen_anime.anime_catalog.databinding.AnimeScreenAnimeAnimeCatalogHeaderBinding
import com.rick.ecatalogs.anime.data_anime.model.UserAnime

class AnimeCatalogAdapter(
    private val onItemClick: (View, Int) -> Unit,
    private val onAnimeFavClick: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (View, View, List<String>) -> Unit
) : PagingDataAdapter<UserAnime, RecyclerView.ViewHolder>(DIFF_UTIL) {

    // The header is a separate adapter in a ConcatAdapter (AnimeHeaderAdapter); it used to take
    // position 0 here, which hid the #1 entry.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeViewHolder.create(parent, onItemClick, onAnimeFavClick, onTranslationClick)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as AnimeViewHolder).bind(it) }
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

class AnimeHeaderViewHolder(private val binding: AnimeScreenAnimeAnimeCatalogHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(
            parent: ViewGroup,
        ): AnimeHeaderViewHolder {
            val itemBinding = AnimeScreenAnimeAnimeCatalogHeaderBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return AnimeHeaderViewHolder(itemBinding)
        }
    }
}
