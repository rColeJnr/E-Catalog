package com.rick.screen_movie.tv_series

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.imdb.series_model.TvSeries
import com.rick.screen_movie.databinding.MovieEntryBinding
import com.rick.screen_movie.util.provideGlide


class TvSeriesAdapter(
    private val onItemClicked: (view: View, series: TvSeries) -> Unit,
    private val onFavClicked: (view: View, favorite: Favorite) -> Unit
) : RecyclerView.Adapter<TvSeriesViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<TvSeries>() {
        override fun areItemsTheSame(oldItem: TvSeries, newItem: TvSeries): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TvSeries, newItem: TvSeries): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvSeriesViewHolder {
        return TvSeriesViewHolder.create(parent, onItemClicked, onFavClicked)
    }

    override fun onBindViewHolder(holder: TvSeriesViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int =
        differ.currentList.size
}

class TvSeriesViewHolder(
    itemBinding: MovieEntryBinding,
    private val onItemClicked: (view: View, series: TvSeries) -> Unit,
    private val onFavClicked: (view: View, favorite: Favorite) -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {
    private val title = itemBinding.movieName
    private val image = itemBinding.movieImage
    private val cast = itemBinding.movieSummary
    private val rating = itemBinding.movieRating

    private lateinit var tvSeries: TvSeries

    init {
        itemBinding.root.setOnClickListener{
            onItemClicked(it, tvSeries)
        }
        itemBinding.favButton.setOnClickListener {
            onFavClicked(it, tvSeries.toFavorite())
        }
    }

    fun bind(series: TvSeries) {
        this.tvSeries = series

        val src = series.image
        if (src.isNotBlank()) provideGlide(this.image, src)
        this.title.text = series.title
        this.cast.text = series.cast
        this.rating.text = series.imDbRating

    }

//    override fun onClick(v: View) {
//        onItemClicked(v, tvSeries)
//    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (view: View, series: TvSeries) -> Unit,
            onFavClick: (view: View, favorite: Favorite) -> Unit
        ): TvSeriesViewHolder {
            val itemBinding =
                MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TvSeriesViewHolder(itemBinding, onItemClick, onFavClick)
        }
    }
}