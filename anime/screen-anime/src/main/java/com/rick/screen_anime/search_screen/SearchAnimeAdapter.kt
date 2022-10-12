package com.rick.screen_anime.search_screen

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_anime.model_anime.Anime
import com.rick.data_anime.model_manga.Manga

class SearchAnimeAdapter(
    private val onAnimeClick: (view: View, anime: Anime) -> Unit,
    private val onMangaClick: (view: View, manga: Manga) -> Unit,
): RecyclerView.Adapter<SearchMangaViewHolder>() {

    

}