package com.rick.ecatalogs.anime.screen_anime.manga_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.anime.screen_anime.manga_catalog.databinding.AnimeScreenAnimeMangaCatalogHeaderBinding
import com.rick.ecatalogs.anime.data_anime.model.UserManga

class MangaCatalogAdapter(
    private val onItemClick: (View, Int) -> Unit,
    private val onMangaFavClick: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (TextView, TextView, List<String>) -> Unit
) : PagingDataAdapter<UserManga, RecyclerView.ViewHolder>(DIFF_UTIL) {

    // The header is a separate adapter in a ConcatAdapter (MangaHeaderAdapter); it used to take
    // position 0 here, which hid the #1 manga.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as MangaViewHolder).bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MangaViewHolder.create(
            parent = parent,
            onItemClick = onItemClick,
            onFavClick = onMangaFavClick,
            onTranslationClick = onTranslationClick
        )

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
