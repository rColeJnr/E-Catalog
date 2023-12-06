package com.rick.screen_movie.tvdetails_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_movie.tmdb.tv.TvResponse
import com.rick.screen_movie.databinding.MovieEntryBinding
import com.rick.screen_movie.util.provideGlide

class TvDetailsAdapter(
    private val onItemClick: (View, TvResponse) -> Unit,
    private val onFavClick: (View, TvResponse) -> Unit
): PagingDataAdapter<TvResponse, TvDetailsViewHolder>(DIFF_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvDetailsViewHolder {
        val binding = MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TvDetailsViewHolder(binding, onItemClick, onFavClick)
    }

    override fun onBindViewHolder(holder: TvDetailsViewHolder, position: Int) {
        val tv = getItem(position)
        holder.bind(tv!!)
    }

    companion object {
        private val DIFF_COMPARATOR = object : DiffUtil.ItemCallback<TvResponse>() {
            override fun areItemsTheSame(oldItem: TvResponse, newItem: TvResponse): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TvResponse, newItem: TvResponse): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class TvDetailsViewHolder(
    binding: MovieEntryBinding,
    private val onItemClick: (View, TvResponse) -> Unit,
    private val onFavClick: (View, TvResponse) -> Unit
): RecyclerView.ViewHolder(binding.root) {
    private val image = binding.movieImage
    private val title = binding.movieName
    private val summary = binding.movieSummary
    private val favorite = binding.favButton
    private val card = binding.movieEntryCardView

    private lateinit var tv: TvResponse

    fun bind(tv: TvResponse) {
        this.tv = tv
        if (tv.image.isNotEmpty()) provideGlide(image, tv.image)
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