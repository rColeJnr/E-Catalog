package com.rick.movie.screen_movie.trending_series_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_movie.UserTrendingSeries
import com.rick.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.movie.screen_movie.common.util.provideGlide
import com.rick.movie.screen_movie.trending_series_catalog.databinding.MovieScreenMovieTrendingSeriesCatalogHeaderBinding
import com.rick.movie.screen_movie.trending_series_catalog.databinding.MovieScreenMovieTrendingSeriesCatalogMovieEntryBinding


class TrendingSeriesAdapter(
    private val onItemClicked: (Int) -> Unit,
    private val onFavClicked: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (View, List<String>) -> Unit
) : PagingDataAdapter<UserTrendingSeries, RecyclerView.ViewHolder>(RESULT_COMPARATOR) {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_SERIES
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER)
            TrendingSeriesHeaderViewHolder.create(parent)
        else
            TrendingSeriesViewHolder.create(
                parent = parent,
                onItemClick = onItemClicked,
                onFavClick = onFavClicked,
                onTranslationClick = onTranslationClick
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TrendingSeriesHeaderViewHolder -> {}
            is TrendingSeriesViewHolder -> {
                val book = getItem(position - 1)
                book?.let { holder.bind(it) }
            }
        }
    }

//    override fun getItemCount(): Int {
//        val actualCount = super.getItemCount()
//        return if (actualCount == 0) 0 else actualCount
//    }

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

        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_SERIES = 1
    }
}

class TrendingSeriesViewHolder(
    itemBinding: MovieScreenMovieTrendingSeriesCatalogMovieEntryBinding,
    private val onItemClicked: (Int) -> Unit,
    private val onFavClicked: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (View, List<String>) -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {
    private val title = itemBinding.movieName
    private val image = itemBinding.movieImage
    private val overview = itemBinding.movieSummary
    private val favorite = itemBinding.favorite
    private val showTranslation = itemBinding.showTranslation
    private val resources = itemView.resources

    private lateinit var trendingSeries: UserTrendingSeries

    init {
        itemBinding.root.setOnClickListener {
            onItemClicked(trendingSeries.id)
        }
        favorite.setOnClickListener {
            onFavClicked(it, trendingSeries.id, trendingSeries.isFavorite)
        }
        showTranslation.setOnClickListener {
            onTranslationClick(overview, listOf(trendingSeries.overview))
        }
    }

    fun bind(series: UserTrendingSeries) {
        this.trendingSeries = series

        val src = series.image
        if (src.isNotBlank()) provideGlide(this.image, getTmdbImageUrl(src))
        this.title.text = series.name
        this.overview.text = series.overview
        favorite.setImageResource(
            if (series.isFavorite) {
                R.drawable.movie_screen_movie_trending_series_catalog_star_filled
            } else {
                R.drawable.movie_screen_movie_trending_series_catalog_star_outlined
            }
        )

    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (Int) -> Unit,
            onFavClick: (View, Int, Boolean) -> Unit,
            onTranslationClick: (View, List<String>) -> Unit
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

class TrendingSeriesHeaderViewHolder(private val binding: MovieScreenMovieTrendingSeriesCatalogHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(
            parent: ViewGroup,
        ): TrendingSeriesHeaderViewHolder {
            val itemBinding = MovieScreenMovieTrendingSeriesCatalogHeaderBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return TrendingSeriesHeaderViewHolder(itemBinding)
        }
    }
}