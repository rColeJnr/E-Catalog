package com.rick.ecatalogs.movie.screen_movie.trending_series_catalog

import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.rick.ecatalogs.data.ui_design.POSTER_KIND_SERIES
import com.rick.ecatalogs.data.ui_design.posterTransitionName
import androidx.core.view.ViewCompat
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.movie.data_movie.model.UserTrendingSeries
import com.rick.ecatalogs.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide
import com.rick.ecatalogs.movie.screen_movie.trending_series_catalog.databinding.MovieScreenMovieTrendingSeriesCatalogMovieEntryBinding


class TrendingSeriesAdapter(
    private val onItemClicked: (View, Int) -> Unit,
    private val onFavClicked: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (TextView, TextView, List<String>) -> Unit
) : PagingDataAdapter<UserTrendingSeries, RecyclerView.ViewHolder>(RESULT_COMPARATOR) {

    // The screen's header is a separate adapter in a ConcatAdapter; it used to take position 0
    // here, which hid the #1 trending series.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TrendingSeriesViewHolder.create(
            parent = parent,
            onItemClick = onItemClicked,
            onFavClick = onFavClicked,
            onTranslationClick = onTranslationClick
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as TrendingSeriesViewHolder).bind(it) }
    }

    companion object {
        private val RESULT_COMPARATOR = object : DiffUtil.ItemCallback<UserTrendingSeries>() {
            override fun areItemsTheSame(
                oldItem: UserTrendingSeries,
                newItem: UserTrendingSeries
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: UserTrendingSeries,
                newItem: UserTrendingSeries
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class TrendingSeriesViewHolder(
    itemBinding: MovieScreenMovieTrendingSeriesCatalogMovieEntryBinding,
    private val onItemClicked: (View, Int) -> Unit,
    private val onFavClicked: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (TextView, TextView, List<String>) -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {
    private val title = itemBinding.movieName
    private val image = itemBinding.movieImage
    private val releaseYear = itemBinding.movieReleaseYear
    private val rating = itemBinding.movieRating
    private val overview = itemBinding.movieSummary
    private val favorite = itemBinding.favorite
    private val showTranslation = itemBinding.showTranslation
    private val resources = itemView.resources
    private var location: String = java.util.Locale.getDefault().language.lowercase()

    private lateinit var trendingSeries: UserTrendingSeries

    init {
        itemBinding.root.setOnClickListener {
            onItemClicked(image, trendingSeries.id)
        }
        favorite.setOnClickListener {
            onFavClicked(it, trendingSeries.id, trendingSeries.isFavorite)
        }
        showTranslation.setOnClickListener {
            onTranslationClick(showTranslation, overview, listOf(trendingSeries.overview))
        }
    }

    fun bind(series: UserTrendingSeries) {
        this.trendingSeries = series
        ViewCompat.setTransitionName(image, posterTransitionName(POSTER_KIND_SERIES, trendingSeries.id))

        val src = series.image
        if (src.isNotBlank()) provideGlide(this.image, getTmdbImageUrl(src))
        this.title.text = series.name
        bindPosterChips(releaseYear, rating, series.firstAirDate, series.voteAverage)
        this.overview.text = series.overview
        favorite.setImageResource(
            if (series.isFavorite) {
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
        fun create(
            parent: ViewGroup,
            onItemClick: (View, Int) -> Unit,
            onFavClick: (View, Int, Boolean) -> Unit,
            onTranslationClick: (TextView, TextView, List<String>) -> Unit
        ): TrendingSeriesViewHolder {
            val itemBinding =
                MovieScreenMovieTrendingSeriesCatalogMovieEntryBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            return TrendingSeriesViewHolder(
                itemBinding,
                onItemClick,
                onFavClick,
                onTranslationClick
            )
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
