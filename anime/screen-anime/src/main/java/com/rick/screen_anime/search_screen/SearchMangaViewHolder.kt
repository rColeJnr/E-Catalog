package com.rick.screen_anime.search_screen

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_anime.model_anime.Anime
import com.rick.data_anime.model_manga.Manga
import com.rick.screen_anime.databinding.JikanEntryBinding

class SearchMangaViewHolder(
    binding: JikanEntryBinding,
    private val onAnimeClick: (view: View, anime: Anime) -> Unit,
    private val onMangaClick: (view: View, manga: Manga) -> Unit
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val pgRating = binding.pgRating

    private var manga: Manga? = null
    private var anime: Anime? = null

    fun bind() {

    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

}
