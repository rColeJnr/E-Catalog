package com.rick.screen_movie.article_screen

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.data.model_movie.UserArticle

class ArticleAdapter(
    private val onItemClicked: (View, UserArticle) -> Unit,
    private val onFavClicked: (View, Long, Boolean) -> Unit
) : PagingDataAdapter<UserArticle, ArticleViewHolder>(RESULT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder.create(parent, onItemClicked, onFavClicked)
    }

//    override fun getItemViewType(position: Int): Int {
//        return when (getItem(position)) {
//            is NYMovieUiModel.MovieItem -> R.layout.movie_entry
//            else -> R.layout.recycler_view_separator_item
//        }
//    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(article = it)
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
        private val RESULT_COMPARATOR = object : DiffUtil.ItemCallback<UserArticle>() {
            override fun areItemsTheSame(oldItem: UserArticle, newItem: UserArticle): Boolean {
                return oldItem.id == newItem.id
//                        ) ||
//                        ((oldItem is NYMovieUiModel.SeparatorItem) && (newItem is NYMovieUiModel.SeparatorItem) &&
//                                (oldItem.description == newItem.description))

            }

            override fun areContentsTheSame(oldItem: UserArticle, newItem: UserArticle): Boolean {
                return oldItem == newItem
            }
        }
    }
}