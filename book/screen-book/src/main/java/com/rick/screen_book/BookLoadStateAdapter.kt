package com.rick.screen_book

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class MoviesLoadStateAdapter(private val retry: () -> Unit )
    : LoadStateAdapter<BookLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: bookLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): BookLoadStateViewHolder {
        return bookLoadStateViewHolder.create(parent, retry)
    }
}
