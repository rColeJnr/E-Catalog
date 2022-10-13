package com.rick.screen_anime.search_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_anime.model_manga.Manga
import com.rick.screen_anime.R
import com.rick.screen_anime.databinding.JikanEntryBinding
import com.rick.screen_anime.manga_screen.getListAsString
import com.rick.screen_anime.manga_screen.provideGlide

class SearchMangaViewHolder(
    binding: JikanEntryBinding,
    private val onItemClick: (view: View, manga: Manga) -> Unit
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val pgRating = binding.pgRating


    init {
        binding.root.setOnClickListener(this)
    }

    private lateinit var manga: Manga

    fun bind(manga: Manga) {
        this.manga = manga
        val resources = itemView.resources
        this.title.text = manga.title
        manga.imagesDto.jpg.imageUrl.let { provideGlide(this.image, it) }
        this.synopsis.text = manga.synopsis
        this.pgRating.text = resources.getString(R.string.authors, getListAsString(manga.authors))
    }


    override fun onClick(view: View) {
        onItemClick(view, manga)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (view: View, manga: Manga) -> Unit
        ): SearchMangaViewHolder {
            val itemBinding =
                JikanEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SearchMangaViewHolder(itemBinding, onItemClick)
        }
    }

}
