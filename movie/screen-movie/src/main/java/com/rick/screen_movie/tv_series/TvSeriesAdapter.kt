package com.rick.screen_movie.tv_series

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_movie.favorite.Favorite
import com.rick.screen_movie.databinding.MovieEntryBinding
import com.rick.screen_movie.util.provideGlide


class TvSeriesAdapter(
    private val onItemClicked: (view: View, series: TvSeriesUiState.Series) -> Unit,
    private val onFavClicked: (view: View, favorite: Favorite) -> Unit
) : PagingDataAdapter<TvSeriesUiState.Series, TvSeriesViewHolder>(RESULT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvSeriesViewHolder {
        return TvSeriesViewHolder.create(parent, onItemClicked, onFavClicked)
    }

    override fun onBindViewHolder(holder: TvSeriesViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

//    override fun getItemCount(): Int =
//        differ.currentList.size

    companion object {
        private val RESULT_COMPARATOR = object : DiffUtil.ItemCallback<TvSeriesUiState.Series>(){
            override fun areItemsTheSame(oldItem: TvSeriesUiState.Series, newItem: TvSeriesUiState.Series): Boolean {
                return oldItem.trendingTv.id == newItem.trendingTv.id
            }

            override fun areContentsTheSame(oldItem: TvSeriesUiState.Series, newItem: TvSeriesUiState.Series): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class TvSeriesViewHolder(
    itemBinding: MovieEntryBinding,
    private val onItemClicked: (view: View, series: TvSeriesUiState.Series) -> Unit,
    private val onFavClicked: (view: View, favorite: Favorite) -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {
    private val title = itemBinding.movieName
    private val image = itemBinding.movieImage
    private val cast = itemBinding.movieSummary

    private lateinit var tvSeries: TvSeriesUiState.Series

    init {
        itemBinding.root.setOnClickListener{
            onItemClicked(it, tvSeries)
        }
        itemBinding.favButton.setOnClickListener {
//            onFavClicked(it, tvSeries.toFavorite()) TODO
        }
    }

    fun bind(series: TvSeriesUiState.Series) {
        this.tvSeries = series

        val src = series.trendingTv.backdropPath
        if (src.isNotBlank()) provideGlide(this.image, src)
        this.title.text = series.trendingTv.name
        this.cast.text = series.trendingTv.overview

    }

//    override fun onClick(v: View) {
//        onItemClicked(v, tvSeries)
//    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (view: View, series: TvSeriesUiState.Series) -> Unit,
            onFavClick: (view: View, favorite: Favorite) -> Unit
        ): TvSeriesViewHolder {
            val itemBinding =
                MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TvSeriesViewHolder(itemBinding, onItemClick, onFavClick)
        }
    }
}