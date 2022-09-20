package com.rick.screen_movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.rick.data_movie.ny_times.Movie
import com.rick.screen_movie.databinding.MovieEntryBinding

class MovieCatalogViewHolder(
    binding: MovieEntryBinding,
    private val onItemClicked: (view: View, movie: Movie) -> Unit
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    private val title = binding.movieName
    private val rating = binding.movieRating
    private val image = binding.movieImage
    private val summary = binding.movieSummary
    private val cardView = binding.movieEntryCardView

    private lateinit var movie: Movie

    init {
        binding.root.setOnClickListener(this)
    }

    fun bind(glide: RequestManager, options: RequestOptions, movie: Movie) {
        this.cardView.transitionName = movie.title
        this.movie = movie
        this.title.text = movie.title
        this.summary.text = movie.summary
        val resources = itemView.resources
        if (movie.rating.isNotBlank()) {
            this.rating.text =
                resources.getString(R.string.rated, movie.rating)
            rating.visibility = View.VISIBLE
        } else rating.visibility = View.INVISIBLE
        if (movie.multimedia.src.isNotBlank()) {
            glide
                .load(movie.multimedia.src)
                .apply(options)
                .into(this.image)
        }
    }

    override fun onClick(v: View) {
        onItemClicked(v, movie)
    }

    companion object {
        fun create(parent: ViewGroup, onItemClicked: (view: View, movie: Movie) -> Unit): MovieCatalogViewHolder {
            val itemBinding = MovieEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MovieCatalogViewHolder(
                itemBinding,
                onItemClicked
            )
        }
    }
}