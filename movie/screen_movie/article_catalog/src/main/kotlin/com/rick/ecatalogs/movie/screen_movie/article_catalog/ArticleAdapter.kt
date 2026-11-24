package com.rick.ecatalogs.movie.screen_movie.article_catalog

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.rick.ecatalogs.movie.data_movie.model.UserArticle

class ArticleAdapter(
    private val onItemClicked: (UserArticle) -> Unit,
    private val onFavClicked: (View, String, Boolean) -> Unit,
    private val onTranslationClicked: (UserArticle, List<String>) -> Unit
) : PagingDataAdapter<UserArticle, ArticleViewHolder>(RESULT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder.create(parent, onItemClicked, onFavClicked, onTranslationClicked)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(article = it)
        }
    }

    companion object {
        private val RESULT_COMPARATOR = object : DiffUtil.ItemCallback<UserArticle>() {
            override fun areItemsTheSame(oldItem: UserArticle, newItem: UserArticle): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserArticle, newItem: UserArticle): Boolean {
                return oldItem == newItem
            }
        }
    }
}