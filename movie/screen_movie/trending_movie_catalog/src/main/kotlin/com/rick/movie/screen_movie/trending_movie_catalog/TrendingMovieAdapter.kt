package com.rick.movie.screen_movie.trending_movie_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_movie.UserTrendingMovie
import com.rick.movie.screen_movie.common.util.provideGlide
import com.rick.movie.screen_movie.trending_movie_catalog.databinding.MovieScreenMovieTrendingMovieMovieEntryBinding
import com.rick.screen_movie.util.getTmdbImageUrl

class TrendingMovieAdapter(
    private val onItemClick: (View, UserTrendingMovie) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit
) : PagingDataAdapter<UserTrendingMovie, TrendingMovieViewHolder>(DIFF_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingMovieViewHolder {
        return TrendingMovieViewHolder.create(parent, onItemClick, onFavClick)
    }

    override fun onBindViewHolder(holder: TrendingMovieViewHolder, position: Int) {
        val movie = (getItem(position))
        movie?.let { holder.bind(it) }
    }

    companion object {
        private val DIFF_COMPARATOR = object : DiffUtil.ItemCallback<UserTrendingMovie>() {
            override fun areItemsTheSame(
                oldItem: UserTrendingMovie,
                newItem: UserTrendingMovie
            ): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(
                oldItem: UserTrendingMovie,
                newItem: UserTrendingMovie
            ): Boolean =
                oldItem == newItem

        }
    }
}

class TrendingMovieViewHolder(
    binding: MovieScreenMovieTrendingMovieMovieEntryBinding,
    private val onItemClick: (View, UserTrendingMovie) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.movieImage
    private val title = binding.movieName
    private val summary = binding.movieSummary
    private val favorite = binding.favButton
    private val cardView = binding.movieEntryCardView
    private val resources = itemView.resources

    private lateinit var movie: UserTrendingMovie

    init {
        cardView.setOnClickListener {
            onItemClick(it, movie)
        }

        favorite.setOnClickListener {
            onFavClick(it, movie.id, movie.isFavorite)
        }
    }

    fun bind(movie: UserTrendingMovie) {
        this.movie = movie
        if (movie.image.isNotEmpty()) provideGlide(image, getTmdbImageUrl(movie.image))
        title.text = movie.title
        summary.text = movie.overview
        favorite.foreground = if (movie.isFavorite) {
            ResourcesCompat.getDrawable(resources, R.drawable.fav_filled_icon, null)
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.fav_outline_icon, null)
        }
    }

    companion object {
        internal fun create(
            parent: ViewGroup,
            onItemClick: (View, UserTrendingMovie) -> Unit,
            onFavClick: (View, Int, Boolean) -> Unit
        ): TrendingMovieViewHolder {
            val binding =
                MovieScreenMovieTrendingMovieMovieEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return TrendingMovieViewHolder(binding, onItemClick, onFavClick)
        }
    }
}