package com.rick.screen_movie.nymovie_screen

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data_movie.ny_times.article_models.NYMovie

class MovieCatalogAdapter(
    private val onItemClicked: (view: View, movie: NYMovie) -> Unit,
    private val onFavClicked: (view: View, favorite: NYMovie) -> Unit
) : PagingDataAdapter<NYMovieUiModel.MovieItem, MovieCatalogViewHolder>(RESULT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCatalogViewHolder {
        return  MovieCatalogViewHolder.create(parent, onItemClicked, onFavClicked)
    }

//    override fun getItemViewType(position: Int): Int {
//        return when (getItem(position)) {
//            is NYMovieUiModel.MovieItem -> R.layout.movie_entry
//            else -> R.layout.recycler_view_separator_item
//        }
//    }

    override fun onBindViewHolder(holder: MovieCatalogViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(movie = it.movie)
        }
//        uiModel.let {
//            when (uiModel) {
//                is NYMovieUiModel.MovieItem ->
//                )
//                is NYMovieUiModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(uiModel.description)
//                else -> null
//            }
//        }
    }

    companion object {
        private val RESULT_COMPARATOR = object : DiffUtil.ItemCallback<NYMovieUiModel.MovieItem>() {
            override fun areItemsTheSame(oldItem: NYMovieUiModel.MovieItem, newItem: NYMovieUiModel.MovieItem): Boolean {
                return oldItem.movie.id == newItem.movie.id
//                        ) ||
//                        ((oldItem is NYMovieUiModel.SeparatorItem) && (newItem is NYMovieUiModel.SeparatorItem) &&
//                                (oldItem.description == newItem.description))

            }

            override fun areContentsTheSame(oldItem: NYMovieUiModel.MovieItem, newItem: NYMovieUiModel.MovieItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}