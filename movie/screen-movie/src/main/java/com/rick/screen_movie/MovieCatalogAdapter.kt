package com.rick.screen_movie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rick.data_movie.Result

class MovieCatalogAdapter(
    private val activity: Activity,
    private val onItemClicked: (Int) -> Unit
) :
    RecyclerView.Adapter<MovieCatalogAdapter.MovieCatalogViewHolder>() {

    private val moviesDiffUtil = object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.summary == newItem.summary
        }
    }

    val moviesDiffer = AsyncListDiffer(this, moviesDiffUtil)

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
        val movie = moviesDiffer.currentList[position]
        with(holder) {
            this.title.text = movie.title
            if (movie.rating.isNotBlank()) {
                this.rating.text =
                    activity.getString(R.string.rated, movie.rating)
                rating.visibility = View.VISIBLE
            } else rating.visibility = View.INVISIBLE
            if (movie.multimedia.src.isNotBlank()) {
                Glide.with(activity)
                    .load(movie.multimedia.src)
                    .into(this.image)
            }
        }
    }

    override fun getItemCount(): Int = moviesDiffer.currentList.size
}