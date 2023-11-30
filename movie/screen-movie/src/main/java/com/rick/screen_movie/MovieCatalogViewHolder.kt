package com.rick.screen_movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.ny_times.article_models.Doc
import com.rick.screen_movie.databinding.MovieEntryBinding
import com.rick.screen_movie.util.provideGlide

class MovieCatalogViewHolder(
    binding: MovieEntryBinding,
    private val onItemClicked: (view: View, movie: Doc) -> Unit,
    private val onFavClicked: (view: View, favorite: Favorite) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.movieName
    private val image = binding.movieImage
    private val summary = binding.movieSummary

    private lateinit var movie: Doc

    init {
        binding.root.setOnClickListener {
            onItemClicked(it, movie)
        }
        binding.favButton.setOnClickListener {
//            onFavClicked(it, movie.toFavorite())
        }
    }

    fun bind(movie: Doc) {
        this.movie = movie
        this.title.text = movie.headline.main
        this.summary.text = movie.snippet
        val resources = itemView.resources
        //TODO (Cheeck rating in nytimes response)
        val src = movie.multimedia[0].url
        if (src.isNotBlank()) provideGlide(this.image, src)
    }

//    override fun onClick(v: View) {
//        onItemClicked(v, movie)
//    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClicked: (view: View, movie: Doc) -> Unit,
            onFavClicked: (view: View, favorite: Favorite) -> Unit
        ): MovieCatalogViewHolder {
            val itemBinding = MovieEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MovieCatalogViewHolder(
                itemBinding,
                onItemClicked,
                onFavClicked
            )
        }
    }
}