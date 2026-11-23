package com.rick.ecatalogs.anime.screen_anime.common

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.rick.ecatalogs.anime.screen_anime.common.JikanLoadStateViewHolder

class JikanLoadStateAdapter(private val retry: () -> Unit )
    : LoadStateAdapter<JikanLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: JikanLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): JikanLoadStateViewHolder {
        return JikanLoadStateViewHolder.create(parent, retry)
    }
}
