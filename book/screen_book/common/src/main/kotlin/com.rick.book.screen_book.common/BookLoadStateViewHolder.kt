package com.rick.book.screen_book.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.rick.book.screen_book.common.databinding.BookScreenBookCommonLoadStateFooterBinding

class BookLoadStateViewHolder(
    private val binding: BookScreenBookCommonLoadStateFooterBinding,
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
        fun create(parent: ViewGroup, retry: () -> Unit): BookLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_screen_book_common_load_state_footer, parent, false)
            val binding = BookScreenBookCommonLoadStateFooterBinding.bind(view)
            return BookLoadStateViewHolder(binding, retry)
        }
    }

}