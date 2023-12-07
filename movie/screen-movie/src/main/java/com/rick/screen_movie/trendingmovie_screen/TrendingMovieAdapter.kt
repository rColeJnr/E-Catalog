package com.rick.screen_movie.trendingmovie_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_movie.tmdb.trending_movie.TrendingMovie
import com.rick.screen_movie.databinding.MovieEntryBinding
import com.rick.screen_movie.util.getTmdbImageUrl
import com.rick.screen_movie.util.provideGlide

class TrendingMovieAdapter(
    private val onItemClick: (View, TrendingMovie) -> Unit,
    private val onFavClick: (View, TrendingMovie) -> Unit
) : PagingDataAdapter<TrendingMovie, TrendingMovieViewHolder>(DIFF_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingMovieViewHolder {
        return TrendingMovieViewHolder.create(parent, onItemClick, onFavClick)
    }

    override fun onBindViewHolder(holder: TrendingMovieViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    companion object {
        private val DIFF_COMPARATOR = object : DiffUtil.ItemCallback<TrendingMovie>() {
            override fun areItemsTheSame(
                oldItem: TrendingMovie,
                newItem: TrendingMovie
            ): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(
                oldItem: TrendingMovie,
                newItem: TrendingMovie
            ): Boolean =
                oldItem == newItem

        }
    }
}

class TrendingMovieViewHolder(
    binding: MovieEntryBinding,
    private val onItemClick: (View, TrendingMovie) -> Unit,
    private val onFavClick: (View, TrendingMovie) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.movieImage
    private val title = binding.movieName
    private val summary = binding.movieSummary
    private val favorite = binding.favButton
    private val cardView = binding.movieEntryCardView

    private lateinit var movie: TrendingMovie

    init {
        cardView.setOnClickListener {
            onItemClick(it, movie)
        }

        favorite.setOnClickListener {
            onFavClick(it, movie)
        }
    }

    fun bind(movie: TrendingMovie) {
        this.movie = movie
        if (movie.image.isNotEmpty()) provideGlide(image, getTmdbImageUrl(movie.image))
        title.text = movie.title
        summary.text = movie.summary
    }

    companion object {
        internal fun create(
            parent: ViewGroup,
            onItemClick: (View, TrendingMovie) -> Unit,
            onFavClick: (View, TrendingMovie) -> Unit
        ): TrendingMovieViewHolder {
            val binding = MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false  )
            return TrendingMovieViewHolder(binding, onItemClick, onFavClick)
        }
    }
}