package com.rick.screen_movie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rick.data_movie.Result
import com.rick.data_movie.ResultDto
import com.rick.screen_movie.databinding.MovieEntryBinding

class MovieCatalogAdapter(
    private val activity: Activity,
    private val onItemClicked: (Int) -> Unit
) :
    PagingDataAdapter<ResultDto, MovieCatalogAdapter.MovieCatalogViewHolder>(RESULT_COMPARATOR) {


    inner class MovieCatalogViewHolder(
        binding: MovieEntryBinding,
        private val onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        internal val title = binding.movieName
        internal val rating = binding.movieRating
        internal val image = binding.movieImage

        init {
            binding.root.isClickable = true
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemClicked(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCatalogViewHolder {
        val itemBinding =
            MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieCatalogViewHolder(itemBinding, onItemClicked)
    }

    override fun onBindViewHolder(holder: MovieCatalogViewHolder, position: Int) {
        val movie = getItem(position)!!
        with(holder) {
            this.title.text = movie.display_title
            if (movie.mpaa_rating.isNotBlank()) {
                this.rating.text =
                    activity.getString(R.string.rated, movie.mpaa_rating)
                rating.visibility = View.VISIBLE
            } else rating.visibility = View.INVISIBLE
            if (movie.multimedia.src.isNotBlank()) {
                Glide.with(activity)
                    .load(movie.multimedia.src)
                    .into(this.image)
            }
        }
    }

    companion object {
        private val RESULT_COMPARATOR = object : DiffUtil.ItemCallback<ResultDto>() {
            override fun areItemsTheSame(oldItem: ResultDto, newItem: ResultDto): Boolean {
                return (oldItem.display_title == newItem.display_title || oldItem.summary_short == newItem.summary_short)
            }

            override fun areContentsTheSame(oldItem: ResultDto, newItem: ResultDto): Boolean {
                return oldItem == newItem
            }
        }
    }
}