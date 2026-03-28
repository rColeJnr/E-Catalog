package com.rick.movie.screen_movie.trending_movie_catalog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_movie.UserTrendingMovie
import com.rick.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.movie.screen_movie.common.util.provideGlide
import com.rick.movie.screen_movie.trending_movie_catalog.databinding.MovieScreenMovieTrendingMovieCatalogHeaderBinding
import com.rick.movie.screen_movie.trending_movie_catalog.databinding.MovieScreenMovieTrendingMovieCatalogMovieEntryBinding

class TrendingMovieAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (TextView, TextView, List<String>) -> Unit
) : PagingDataAdapter<UserTrendingMovie, RecyclerView.ViewHolder>(DIFF_COMPARATOR) {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_MOVIE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER)
            TrendingMovieHeaderViewHolder.create(parent)
        else
            TrendingMovieViewHolder.create(parent, onItemClick, onFavClick, onTranslationClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TrendingMovieHeaderViewHolder -> {}
            is TrendingMovieViewHolder -> {
                val movie = getItem(position)
                movie?.let { holder.bind(it) }
            }
        }
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

        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_MOVIE = 1
    }
}

class TrendingMovieViewHolder(
    binding: MovieScreenMovieTrendingMovieCatalogMovieEntryBinding,
    private val onItemClick: (Int) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (TextView, TextView, List<String>) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.movieImage
    private val title = binding.movieName
    private val summary = binding.movieSummary
    private val favorite = binding.favorite
    private val showTranslation = binding.showTranslation
    private val cardView = binding.movieEntryCardView
    private val resources = itemView.resources
    private var location: String = java.util.Locale.getDefault().language.lowercase()

    private lateinit var movie: UserTrendingMovie

    init {
        cardView.setOnClickListener {
            onItemClick(movie.id)
        }

        favorite.setOnClickListener {
            onFavClick(it, movie.id, movie.isFavorite)
        }
        showTranslation.setOnClickListener {
            onTranslationClick(showTranslation, summary, listOf(movie.overview))
            it.visibility = View.GONE
        }
    }

    fun bind(movie: UserTrendingMovie) {
        this.movie = movie
        if (movie.image.isNotEmpty()) provideGlide(image, getTmdbImageUrl(movie.image))
        title.text = movie.title
        summary.text = movie.overview
        favorite.setImageResource(
            if (movie.isFavorite) {
                R.drawable.movie_screen_movie_trending_movie_catalog_star_filled
            } else {
                R.drawable.movie_screen_movie_trending_movie_catalog_star_outlined
            }
        )

        if (location == "en") {
            showTranslation.visibility = View.GONE
        } else {
            showTranslation.visibility = View.VISIBLE
        }
    }

    companion object {
        internal fun create(
            parent: ViewGroup,
            onItemClick: (Int) -> Unit,
            onFavClick: (View, Int, Boolean) -> Unit,
            onTranslationClick: (TextView, TextView, List<String>) -> Unit
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

class TrendingMovieHeaderViewHolder(private val binding: MovieScreenMovieTrendingMovieCatalogHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(
            parent: ViewGroup,
        ): TrendingMovieHeaderViewHolder {
            val itemBinding = MovieScreenMovieTrendingMovieCatalogHeaderBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return TrendingMovieHeaderViewHolder(itemBinding)
        }
    }
}