package com.rick.book.screen_book.common

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class BookLoadStateAdapter(private val retry: () -> Unit )
    : LoadStateAdapter<BookLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: BookLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): BookLoadStateViewHolder {
        return BookLoadStateViewHolder.create(parent, retry)
    }
}
