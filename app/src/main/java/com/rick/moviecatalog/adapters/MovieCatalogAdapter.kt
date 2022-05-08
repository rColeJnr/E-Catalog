package com.rick.moviecatalog.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rick.moviecatalog.MainActivity
import com.rick.moviecatalog.data.model.Result
import com.rick.moviecatalog.databinding.MovieEntryBinding

class MovieCatalogAdapter (private val activity: MainActivity) :
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

    inner class MovieCatalogViewHolder(binding: MovieEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        internal val title = binding.movieName
        internal val rating = binding.movieRating
        internal val image = binding.movieImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCatalogViewHolder {
//        return MovieCatalogViewHolder(
//            LayoutInflater.from(parent.context).inflate((R.layout.movie_entry), parent, false)
//        )
        val itemBinding = MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieCatalogViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MovieCatalogViewHolder, position: Int) {
        val movie = moviesDiffer.currentList[position]
        with(holder) {
            this.title.text = movie.title
            this.rating.text = movie.rating
            if (movie.multimedia.src.isNotBlank()) {
                Glide.with(activity)
                    .load(movie.multimedia.src)
                    .into(this.image)
            }
        }
    }

    override fun getItemCount(): Int = moviesDiffer.currentList.size
}