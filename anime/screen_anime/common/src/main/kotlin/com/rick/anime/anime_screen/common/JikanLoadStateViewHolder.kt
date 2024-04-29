package com.rick.anime.anime_screen.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.rick.anime.screen_anime.common.R
import com.rick.anime.screen_anime.common.databinding.AnimeScreenAnimeCommonLoadStateFooterBinding

class JikanLoadStateViewHolder(
    private val binding: AnimeScreenAnimeCommonLoadStateFooterBinding,
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
        fun create(parent: ViewGroup, retry: () -> Unit): JikanLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.anime_screen_anime_common_load_state_footer, parent, false)
            val binding = AnimeScreenAnimeCommonLoadStateFooterBinding.bind(view)
            return JikanLoadStateViewHolder(binding, retry)
        }
    }

}