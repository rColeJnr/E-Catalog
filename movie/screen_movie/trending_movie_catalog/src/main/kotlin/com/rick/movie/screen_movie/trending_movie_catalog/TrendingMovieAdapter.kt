package com.rick.movie.screen_movie.trending_movie_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_movie.UserTrendingMovie
import com.rick.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.movie.screen_movie.common.util.provideGlide
import com.rick.movie.screen_movie.trending_movie_catalog.databinding.MovieScreenMovieTrendingMovieCatalogMovieEntryBinding

class TrendingMovieAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (View, List<String>) -> Unit
) : PagingDataAdapter<UserTrendingMovie, TrendingMovieViewHolder>(DIFF_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingMovieViewHolder {
        return TrendingMovieViewHolder.create(parent, onItemClick, onFavClick, onTranslationClick)
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
    binding: MovieScreenMovieTrendingMovieCatalogMovieEntryBinding,
    private val onItemClick: (Int) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (View, List<String>) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.movieImage
    private val title = binding.movieName
    private val summary = binding.movieSummary
    private val favorite = binding.favButton
    private val showTranslation = binding.showTranslation
    private val showOriginal = binding.showOriginal
    private val cardView = binding.movieEntryCardView
    private val resources = itemView.resources

    private lateinit var movie: UserTrendingMovie

    init {
        cardView.setOnClickListener {
            onItemClick(movie.id)
        }

        favorite.setOnClickListener {
            onFavClick(it, movie.id, movie.isFavorite)
        }
        showTranslation.setOnClickListener {
            onTranslationClick(summary, listOf(movie.overview))
            it.visibility = View.GONE
            showOriginal.visibility = View.VISIBLE

        }
        showOriginal.setOnClickListener {
            summary.text = movie.overview
            it.visibility = View.GONE
            showTranslation.visibility = View.VISIBLE
        }
    }

    fun bind(movie: UserTrendingMovie) {
        this.movie = movie
        if (movie.image.isNotEmpty()) provideGlide(image, getTmdbImageUrl(movie.image))
        title.text = movie.title
        summary.text = movie.overview
        favorite.foreground = if (movie.isFavorite) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.movie_screen_movie_trending_movie_catalog_ic_fav_filled,
                null
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.movie_screen_movie_trending_movie_catalog_ic_fav_outlined,
                null
            )
        }
    }

    companion object {
        internal fun create(
            parent: ViewGroup,
            onItemClick: (Int) -> Unit,
            onFavClick: (View, Int, Boolean) -> Unit,
            onTranslationClick: (View, List<String>) -> Unit
        ): TrendingMovieViewHolder {
            val binding =
                MovieScreenMovieTrendingMovieCatalogMovieEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return TrendingMovieViewHolder(binding, onItemClick, onFavClick, onTranslationClick)
        }
    }
}