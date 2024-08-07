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
import com.rick.movie.screen_movie.trending_series_catalog.databinding.MovieScreenMovieTrendingSeriesCatalogMovieEntryBinding


class TrendingSeriesAdapter(
    private val onItemClicked: (Int) -> Unit,
    private val onFavClicked: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (View, List<String>) -> Unit
) : PagingDataAdapter<UserTrendingSeries, TrendingSeriesViewHolder>(RESULT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingSeriesViewHolder {
        return TrendingSeriesViewHolder.create(
            parent,
            onItemClicked,
            onFavClicked,
            onTranslationClick
        )
    }

    override fun onBindViewHolder(holder: TrendingSeriesViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

//    override fun getItemCount(): Int =
//        differ.currentList.size

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
    private val onItemClicked: (Int) -> Unit,
    private val onFavClicked: (View, Int, Boolean) -> Unit,
    private val onTranslationClick: (View, List<String>) -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {
    private val title = itemBinding.movieName
    private val image = itemBinding.movieImage
    private val overview = itemBinding.movieSummary
    private val favorite = itemBinding.favButton
    private val showTranslation = itemBinding.showTranslation
    private val showOriginal = itemBinding.showOriginal
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
            showOriginal.visibility = View.VISIBLE
            it.visibility = View.GONE
        }
        showOriginal.setOnClickListener {
            overview.text = trendingSeries.overview
            it.visibility = View.GONE
            showTranslation.visibility = View.VISIBLE
        }
    }

    fun bind(series: UserTrendingSeries) {
        this.trendingSeries = series

        val src = series.image
        if (src.isNotBlank()) provideGlide(this.image, getTmdbImageUrl(src))
        this.title.text = series.name
        this.overview.text = series.overview
        this.favorite.foreground = if (series.isFavorite) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.movie_screen_movie_trending_series_catalog_star_filled,
                null
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.movie_screen_movie_trending_series_catalog_star_outlined,
                null
            )
        }

    }

//    override fun onClick(v: View) {
//        onItemClicked(v, tvSeries)
//    }

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