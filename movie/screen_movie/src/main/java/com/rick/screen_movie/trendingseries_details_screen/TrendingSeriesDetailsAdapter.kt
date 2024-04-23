package com.rick.screen_movie.trendingseries_details_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_movie.tmdb.series.SeriesResponse
import com.rick.screen_movie.databinding.MovieEntryBinding
import com.rick.screen_movie.util.getTmdbImageUrl
import com.rick.screen_movie.util.provideGlide

class SeriesDetailsAdapter(
    private val onItemClick: (View, SeriesResponse) -> Unit,
    private val onFavClick: (View, SeriesResponse) -> Unit
) : PagingDataAdapter<SeriesResponse, TvDetailsViewHolder>(DIFF_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvDetailsViewHolder {
        val binding = MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TvDetailsViewHolder(binding, onItemClick, onFavClick)
    }

    override fun onBindViewHolder(holder: TvDetailsViewHolder, position: Int) {
        val tv = getItem(position)
        holder.bind(tv!!)
    }

    companion object {
        private val DIFF_COMPARATOR = object : DiffUtil.ItemCallback<SeriesResponse>() {
            override fun areItemsTheSame(
                oldItem: SeriesResponse,
                newItem: SeriesResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SeriesResponse,
                newItem: SeriesResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class TvDetailsViewHolder(
    binding: MovieEntryBinding,
    private val onItemClick: (View, SeriesResponse) -> Unit,
    private val onFavClick: (View, SeriesResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.movieImage
    private val title = binding.movieName
    private val summary = binding.movieSummary
    private val favorite = binding.favButton
    private val card = binding.movieEntryCardView

    private lateinit var tv: SeriesResponse

    fun bind(tv: SeriesResponse) {
        this.tv = tv
        if (tv.image.isNotEmpty()) provideGlide(image, getTmdbImageUrl(tv.image))
        title.text = tv.name
        summary.text = tv.summary
    }

    init {
        favorite.setOnClickListener {
            onFavClick(it, tv)
        }
        card.setOnClickListener {
            onItemClick(it, tv)
        }
    }

}