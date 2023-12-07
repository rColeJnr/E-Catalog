package com.rick.screen_movie.nymovie_screen

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rick.data_movie.ny_times.article_models.NYMovie
import com.rick.screen_movie.R
import com.rick.screen_movie.SeparatorViewHolder

class MovieCatalogAdapter(
    private val onItemClicked: (view: View, movie: NYMovie) -> Unit,
    private val onFavClicked: (view: View, favorite: NYMovie) -> Unit
) : PagingDataAdapter<NYMovieUiModel, ViewHolder>(RESULT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == R.layout.movie_entry)
            MovieCatalogViewHolder.create(parent, onItemClicked, onFavClicked)
        else SeparatorViewHolder.create(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NYMovieUiModel.MovieItem -> R.layout.movie_entry
            else -> R.layout.recycler_view_separator_item
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is NYMovieUiModel.MovieItem -> (holder as MovieCatalogViewHolder).bind(
                    movie = uiModel.movie,
                )
                is NYMovieUiModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(uiModel.description)
                else -> null
            }
        }
    }

    companion object {
        private val RESULT_COMPARATOR = object : DiffUtil.ItemCallback<NYMovieUiModel>() {
            override fun areItemsTheSame(oldItem: NYMovieUiModel, newItem: NYMovieUiModel): Boolean {
                return ((oldItem is NYMovieUiModel.MovieItem) && (newItem is NYMovieUiModel.MovieItem) &&
                        (oldItem.movie.id == newItem.movie.id)
                        ) ||
                        ((oldItem is NYMovieUiModel.SeparatorItem) && (newItem is NYMovieUiModel.SeparatorItem) &&
                                (oldItem.description == newItem.description))

            }

            override fun areContentsTheSame(oldItem: NYMovieUiModel, newItem: NYMovieUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}