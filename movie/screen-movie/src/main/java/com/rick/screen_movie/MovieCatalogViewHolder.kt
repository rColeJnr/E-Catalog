package com.rick.screen_movie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_movie.Movie
import com.rick.screen_movie.databinding.MovieEntryBinding

class MovieCatalogViewHolder(
    binding: MovieEntryBinding,
    private val onItemClicked: (Movie) -> Unit
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    private val title = binding.movieName
    private val rating = binding.movieRating
    private val image = binding.movieImage

    private lateinit var movie: Movie

    init {
        binding.root.setOnClickListener(this)
    }

    fun bind(movie: Movie, activity: Activity) {
        this.movie = movie
        this.title.text = movie.title
        if (movie.rating.isNotBlank()) {
            this.rating.text =
                activity.getString(R.string.rated, movie.rating)
            rating.visibility = View.VISIBLE
        } else rating.visibility = View.INVISIBLE
        if (movie.multimedia.src.isNotBlank()) {
            com.bumptech.glide.Glide.with(activity)
                .load(movie.multimedia.src)
                .into(this.image)
        }
    }

    override fun onClick(v: View?) {
        onItemClicked(movie)
    }

    companion object {
        fun create(parent: ViewGroup, onItemClicked: (Movie) -> Unit): MovieCatalogViewHolder {
            val itemBinding =
                MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MovieCatalogViewHolder(
                itemBinding,
                onItemClicked
            )
        }
    }
}