package com.rick.screen_movie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_movie.Result
import com.rick.screen_movie.databinding.MovieEntryBinding

class MovieCatalogViewHolder(
    binding: MovieEntryBinding,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    internal val title = binding.movieName
    internal val rating = binding.movieRating
    internal val image = binding.movieImage

    init {
        binding.root.setOnClickListener{
            // TODO (do navigation here?)
        }
    }

    fun bind(movie: Result, activity: Activity) {
        this.title.text = movie.title
        if (movie.rating.isNotBlank()) {
            this.rating.text =
                activity.getString(com.rick.screen_movie.R.string.rated, movie.rating)
            rating.visibility = android.view.View.VISIBLE
        } else rating.visibility = android.view.View.INVISIBLE
        if (movie.multimedia.src.isNotBlank()) {
            com.bumptech.glide.Glide.with(activity)
                .load(movie.multimedia.src)
                .into(this.image)
        }
    }

    override fun onClick(v: View?) {

//        onItemClicked(bindingAdapterPosition)
    }

    companion object {
        fun create(parent: ViewGroup): MovieCatalogViewHolder {
            val itemBinding =
                MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MovieCatalogViewHolder(
                itemBinding,
            )
        }
    }
}