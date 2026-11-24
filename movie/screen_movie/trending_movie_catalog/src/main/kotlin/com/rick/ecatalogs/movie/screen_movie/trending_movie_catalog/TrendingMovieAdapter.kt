package com.rick.ecatalogs.movie.screen_movie.trending_movie_catalog

import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.rick.ecatalogs.data.ui_design.POSTER_KIND_MOVIE
import com.rick.ecatalogs.data.ui_design.posterTransitionName
import androidx.core.view.ViewCompat
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.movie.data_movie.model.UserTrendingMovie
import com.rick.ecatalogs.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide
import com.rick.ecatalogs.movie.screen_movie.trending_movie_catalog.databinding.MovieScreenMovieTrendingMovieCatalogMovieEntryBinding

class TrendingMovieAdapter(
    private val onItemClick: (View, Int) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (TextView, TextView, List<String>) -> Unit
) : PagingDataAdapter<UserTrendingMovie, RecyclerView.ViewHolder>(DIFF_COMPARATOR) {

    // The screen's header (title, browse rows, genres) is a separate adapter in a ConcatAdapter;
    // it used to take position 0 here, which hid the #1 trending movie.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TrendingMovieViewHolder.create(parent, onItemClick, onFavClick, onTranslationClick)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as TrendingMovieViewHolder).bind(it) }
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
    private val onItemClick: (View, Int) -> Unit,
    private val onFavClick: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (TextView, TextView, List<String>) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.movieImage
    private val releaseYear = binding.movieReleaseYear
    private val rating = binding.movieRating
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
            onItemClick(image, movie.id)
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
        ViewCompat.setTransitionName(image, posterTransitionName(POSTER_KIND_MOVIE, movie.id))
        if (movie.image.isNotEmpty()) provideGlide(image, getTmdbImageUrl(movie.image))
        title.text = movie.title
        bindPosterChips(releaseYear, rating, movie.releaseDate, movie.voteAverage)
        summary.text = movie.overview
        favorite.setImageResource(
            if (movie.isFavorite) {
                com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_filled
            } else {
                com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_outlined
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
            onItemClick: (View, Int) -> Unit,
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

/** Fills the year / rating chips on a poster card, hiding a chip when there's nothing to show. */
internal fun bindPosterChips(year: TextView, rating: TextView, date: String, voteAverage: Double) {
    val releaseYear = date.take(4).takeIf { it.length == 4 && it.all(Char::isDigit) }
    year.text = releaseYear.orEmpty()
    year.isVisible = releaseYear != null
    rating.text = String.format(java.util.Locale.getDefault(), "★ %.1f", voteAverage)
    rating.isVisible = voteAverage > 0.0
}
