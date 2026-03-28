package com.rick.anime.screen_anime.manga_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.anime.screen_anime.manga_catalog.databinding.AnimeScreenAnimeMangaCatalogHeaderBinding
import com.rick.data.model_anime.UserAnime
import com.rick.data.model_anime.UserManga

class MangaCatalogAdapter(
    private val onItemClick: (View, Int) -> Unit,
    private val onMangaFavClick: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (View, List<String>) -> Unit
) : PagingDataAdapter<UserManga, RecyclerView.ViewHolder>(DIFF_UTIL) {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_MANGA
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MangaHeaderViewHolder -> {
            }

            is MangaViewHolder -> {
                val book = getItem(position - 1)
                book?.let { holder.bind(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) MangaHeaderViewHolder.create(parent)
        else MangaViewHolder.create(
            parent = parent,
            onItemClick = onItemClick,
            onFavClick = onMangaFavClick,
            onTranslationClick = onTranslationClick
        )
    }

    override fun getItemCount(): Int {
        val actualCount = super.getItemCount()
        return if (actualCount == 0) 0 else actualCount + 1
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

        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_MANGA = 1
    }
}

class MangaHeaderViewHolder(private val binding: AnimeScreenAnimeMangaCatalogHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(
            parent: ViewGroup,
        ): MangaHeaderViewHolder {
            val itemBinding = AnimeScreenAnimeMangaCatalogHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return MangaHeaderViewHolder(itemBinding)
        }
    }
}