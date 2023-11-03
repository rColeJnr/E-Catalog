package com.rick.screen_movie

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.ny_times_deprecated.Movie

class MovieCatalogAdapter(
    private val onItemClicked: (view: View, movie: Movie) -> Unit,
    private val onFavClicked: (view: View, favorite: Favorite) -> Unit
) :
    PagingDataAdapter<UiModel, ViewHolder>(RESULT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == R.layout.movie_entry)
            MovieCatalogViewHolder.create(parent, onItemClicked, onFavClicked)
        else SeparatorViewHolder.create(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.MovieItem -> R.layout.movie_entry
            else -> R.layout.recycler_view_separator_item
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.MovieItem -> (holder as MovieCatalogViewHolder).bind(
                    movie = uiModel.movie,
                )
                is UiModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(uiModel.description)
                else -> null
            }
        }
    }

    companion object {
        private val RESULT_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return ((oldItem is UiModel.MovieItem) && (newItem is UiModel.MovieItem) &&
                        (oldItem.movie.title == newItem.movie.title)
                        ) ||
                        ((oldItem is UiModel.SeparatorItem) && (newItem is UiModel.SeparatorItem) &&
                                (oldItem.description == newItem.description))

            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}