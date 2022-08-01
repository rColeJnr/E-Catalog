package com.rick.screen_movie

import android.app.Activity
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class MovieCatalogAdapter(
    private val activity: Activity,
    private val onItemClicked: (UiModel) -> Unit
) :
    PagingDataAdapter<UiModel, ViewHolder>(RESULT_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == R.layout.movie_entry) MovieCatalogViewHolder.create(parent)
        else SeparatorViewHolder.create(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.MovieItem -> R.layout.movie_entry
            is UiModel.SeparatorItem -> R.layout.recycler_view_separator_item
            null -> throw java.lang.UnsupportedOperationException("Unknown view")
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when(uiModel){
                is UiModel.MovieItem -> (holder as MovieCatalogViewHolder).bind(uiModel.movie, activity)
                is UiModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(uiModel.description)
            }
        }
    }

    companion object {
        private val RESULT_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.MovieItem && newItem is UiModel.MovieItem &&
                        (oldItem.movie.title == newItem.movie.title || oldItem.movie.summary == newItem.movie.summary)) ||
                        (oldItem is UiModel.SeparatorItem && newItem is UiModel.SeparatorItem &&
                                oldItem.description == newItem.description)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}