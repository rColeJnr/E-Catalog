package com.rick.ecatalogs.movie.screen_movie.trending_series_catalog

import com.rick.ecatalogs.data.ui_components.common.TranslationToggle
import com.rick.ecatalogs.data.translation.TextTranslator
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
    private val translator: TextTranslator,
) : PagingDataAdapter<UserTrendingSeries, RecyclerView.ViewHolder>(RESULT_COMPARATOR) {

    // The screen's header is a separate adapter in a ConcatAdapter; it used to take position 0
    // here, which hid the #1 trending series.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TrendingSeriesViewHolder.create(
            parent = parent,
            onItemClick = onItemClicked,
            onFavClick = onFavClicked,
            translator = translator,
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
    translator: TextTranslator,
) : RecyclerView.ViewHolder(itemBinding.root) {
    private val title = itemBinding.movieName
    private val image = itemBinding.movieImage
    private val releaseYear = itemBinding.movieReleaseYear
    private val rating = itemBinding.movieRating
    private val overview = itemBinding.movieSummary
    private val favorite = itemBinding.favorite
    private val translation = TranslationToggle(itemBinding.showTranslation, overview, translator)
    private val resources = itemView.resources

    private lateinit var trendingSeries: UserTrendingSeries

    init {
        // The poster card is clickable itself (ripple) and takes its taps: listen on it too,
        // not only on the row (which only gets taps on the overview below the card)
        val openSeries = View.OnClickListener { onItemClicked(image, trendingSeries.id) }
        itemBinding.movieEntryCardView.setOnClickListener(openSeries)
        itemBinding.root.setOnClickListener(openSeries)
        favorite.setOnClickListener {
            onFavClicked(it, trendingSeries.id, trendingSeries.isFavorite)
        }
    }

    fun bind(series: UserTrendingSeries) {
        this.trendingSeries = series
        ViewCompat.setTransitionName(image, posterTransitionName(POSTER_KIND_SERIES, trendingSeries.id))

        val src = series.image
        if (src.isNotBlank()) provideGlide(this.image, getTmdbImageUrl(src))
        this.title.text = series.name
        bindPosterChips(releaseYear, rating, series.firstAirDate, series.voteAverage)
        translation.bind(series.overview)
        favorite.setImageResource(
            if (series.isFavorite) {
                com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_filled
            } else {
                com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_outlined
            }
        )
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (View, Int) -> Unit,
            onFavClick: (View, Int, Boolean) -> Unit,
            translator: TextTranslator,
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
                translator,
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
