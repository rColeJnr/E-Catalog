package com.rick.movie.screen_movie.trending_series_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_movie.tmdb.trending_series.TrendingSeries
import com.rick.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.movie.screen_movie.common.util.provideGlide
import com.rick.movie.screen_movie.trending_series_details.databinding.MovieScreenMovieTrendingSeriesDetailsSimilarEntryBinding

class SeriesSimilarDetailsAdapter(
    private val onSimilarClick: (TrendingSeries) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffUtil =
        object : DiffUtil.ItemCallback<TrendingSeries>() {
            override fun areItemsTheSame(
                oldItem: TrendingSeries,
                newItem: TrendingSeries
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TrendingSeries,
                newItem: TrendingSeries
            ): Boolean {
                return oldItem == newItem
            }
        }

    val similarsDiffer = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SeriesSimilarsViewHolder.create(parent, onSimilarClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val similar = similarsDiffer.currentList[position]
        (holder as SeriesSimilarsViewHolder).bind(similar)
    }

    override fun getItemCount(): Int = similarsDiffer.currentList.size
}

class SeriesSimilarsViewHolder(
    binding: MovieScreenMovieTrendingSeriesDetailsSimilarEntryBinding,
    onSimilarClick: (TrendingSeries) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    private val image = binding.similarImage
    private val title = binding.similarTitle
    private val card = binding.root

    private lateinit var similar: TrendingSeries

    init {
        card.setOnClickListener {
            onSimilarClick(similar)
        }
    }

    fun bind(similar: TrendingSeries) {
        this.similar = similar
        this.title.text = similar.name

        val src = similar.image
        if (src.isNotBlank()) provideGlide(this.image, getTmdbImageUrl(src))
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onSimilarClick: (TrendingSeries) -> Unit
        ): SeriesSimilarsViewHolder {
            val itemBinding = MovieScreenMovieTrendingSeriesDetailsSimilarEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return SeriesSimilarsViewHolder(itemBinding, onSimilarClick)
        }
    }
}
