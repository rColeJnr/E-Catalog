package com.rick.anime.screen_anime.anime_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.anime.screen_anime.anime_catalog.databinding.AnimeScreenAnimeAnimeCatalogHeaderBinding
import com.rick.data.model_anime.UserAnime

class AnimeCatalogAdapter(
    private val onItemClick: (View, Int) -> Unit,
    private val onAnimeFavClick: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (View, View, List<String>) -> Unit
) : PagingDataAdapter<UserAnime, RecyclerView.ViewHolder>(DIFF_UTIL) {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ANIME
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AnimeHeaderViewHolder -> {
            }

            is AnimeViewHolder -> {
                val anime = getItem(position )
                anime?.let { holder.bind(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER)
            AnimeHeaderViewHolder.create(parent)
        else
            AnimeViewHolder.create(parent, onItemClick, onAnimeFavClick, onTranslationClick)
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

        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ANIME = 1
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