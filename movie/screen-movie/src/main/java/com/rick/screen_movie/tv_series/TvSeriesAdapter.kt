package com.rick.screen_movie.tv_series

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rick.data_movie.imdb.series_model.TvSeries
import com.rick.screen_movie.databinding.MovieEntryBinding


class TvSeriesAdapter(private val context: Context): RecyclerView.Adapter<TvSeriesViewHolder>() {

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
        return TvSeriesViewHolder.create(context, parent)
    }

    override fun onBindViewHolder(holder: TvSeriesViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int =
        differ.currentList.size
}

class TvSeriesViewHolder(
    private val context: Context,
    itemBinding: MovieEntryBinding
): RecyclerView.ViewHolder(itemBinding.root) {
    private val title = itemBinding.movieName
    private val image = itemBinding.movieImage
    private val cast = itemBinding.movieSummary
    private val rating = itemBinding.movieRating

    private var tvSeries: TvSeries? = null

    fun bind(series: TvSeries) {
        this.tvSeries = series
        if (series.image.isNotEmpty()){
            Glide.with(context).load(series.image).into(this.image)
        }
        this.title.text = series.title
        this.cast.text = series.crew
        this.rating.text = series.imDbRating

    }

    companion object {
        fun create(context: Context, parent: ViewGroup): TvSeriesViewHolder {
            val itemBinding = MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TvSeriesViewHolder(context, itemBinding)
        }
    }
}