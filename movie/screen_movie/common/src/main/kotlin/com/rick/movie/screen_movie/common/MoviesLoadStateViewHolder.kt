package com.rick.movie.screen_movie.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.rick.movie.screen_movie.common.databinding.MovieScreenMovieCommonLoadStateFooterBinding

class MoviesLoadStateViewHolder(
    private val binding: MovieScreenMovieCommonLoadStateFooterBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener {
            retry.invoke()
        }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): MoviesLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_screen_movie_common_load_state_footer, parent, false)
            val binding = MovieScreenMovieCommonLoadStateFooterBinding.bind(view)
            return MoviesLoadStateViewHolder(binding, retry)
        }
    }

}