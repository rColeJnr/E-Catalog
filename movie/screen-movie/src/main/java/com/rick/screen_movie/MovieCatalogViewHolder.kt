package com.rick.screen_movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rick.screen_movie.databinding.MovieEntryBinding

class MovieCatalogViewHolder(
    binding: MovieEntryBinding,
    private val onItemClicked: (UiModel) -> Unit, private val com.
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    internal val title = binding.movieName
    internal val rating = binding.movieRating
    internal val image = binding.movieImage

    init {
        binding.root.isClickable = true
        binding.root.setOnClickListener(this)
    }

    fun bind() {

        with(holder) {
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

    }
    override fun onClick(v: View?) {
        onItemClicked(com.rick.screen_movie.getItem(bindingAdapterPosition)!!)
    }

    companion object {
        fun create(parent: ViewGroup): MovieCatalogViewHolder {
            val itemBinding =
                MovieEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return com.rick.screen_movie.MovieCatalogViewHolder(itemBinding,
                com.rick.screen_movie.onItemClicked
            )
        }
    }
}