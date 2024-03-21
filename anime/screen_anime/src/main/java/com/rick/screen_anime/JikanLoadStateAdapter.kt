package com.rick.screen_anime

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

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
