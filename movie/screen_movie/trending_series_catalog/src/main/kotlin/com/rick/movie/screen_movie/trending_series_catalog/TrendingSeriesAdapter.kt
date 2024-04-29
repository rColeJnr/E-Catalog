package com.rick.movie.screen_movie.trending_series_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_movie.UserTrendingSeries
import com.rick.movie.screen_movie.common.util.provideGlide
import com.rick.movie.screen_movie.trending_series_catalog.databinding.MovieEntryBinding
import com.rick.screen_movie.util.getTmdbImageUrl


class TrendingSeriesAdapter(
    private val onItemClicked: (View, Int) -> Unit,
    private val onFavClicked: (View, Int, Boolean) -> Unit
) : PagingDataAdapter<UserTrendingSeries, TrendingSeriesViewHolder>(RESULT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingSeriesViewHolder {
        return TrendingSeriesViewHolder.create(parent, onItemClicked, onFavClicked)
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
    itemBinding: MovieEntryBinding,
    private val onItemClicked: (View, Int) -> Unit,
    private val onFavClicked: (View, Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {
    private val title = itemBinding.movieName
    private val image = itemBinding.movieImage
    private val cast = itemBinding.movieSummary
    private val favorite = itemBinding.favButton
    private val resources = itemView.resources

    private lateinit var trendingSeries: UserTrendingSeries

    init {
        itemBinding.root.setOnClickListener {
            onItemClicked(it, trendingSeries.id)
        }
        favorite.setOnClickListener {
            onFavClicked(it, trendingSeries.id, trendingSeries.isFavorite)
        }
    }

    fun bind(series: UserTrendingSeries) {
        this.trendingSeries = series

        val src = series.image
        if (src.isNotBlank()) provideGlide(this.image, getTmdbImageUrl(src))
        this.title.text = series.name
        this.cast.text = series.overview
        this.favorite.foreground = if (series.isFavorite) {
            ResourcesCompat.getDrawable(resources, R.drawable.fav_filled_icon, null)
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.fav_outline_icon, null)
        }

    }

//    override fun onClick(v: View) {
//        onItemClicked(v, tvSeries)
//    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (View, Int) -> Unit,
            onFavClick: (View, Int, Boolean) -> Unit
        ): TrendingSeriesViewHolder {
            val itemBinding =
                MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TrendingSeriesViewHolder(itemBinding, onItemClick, onFavClick)
        }
    }
}