package com.rick.movie.screen_movie.article_catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rick.data.model_movie.UserArticle
import com.rick.movie.screen_movie.article_catalog.databinding.MovieEntryBinding
import com.rick.movie.screen_movie.common.util.provideGlide

class ArticleViewHolder(
    binding: MovieEntryBinding,
    private val onItemClicked: (view: View, movie: UserArticle) -> Unit,
    private val onFavClicked: (view: View, Long, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.movieName
    private val image = binding.movieImage
    private val summary = binding.movieSummary
    private val favorite = binding.favButton
    private val resources = itemView.resources

    private lateinit var article: UserArticle

    init {
        binding.root.setOnClickListener {
            onItemClicked(it, article)
        }
        favorite.setOnClickListener {
            onFavClicked(it, article.id, article.isFavorite)
        }
    }

    fun bind(article: UserArticle) {
        this.article = article
        this.title.text = article.headline
        this.summary.text = article.leadParagraph
        val src = article.multimedia
        if (src.isNotBlank()) provideGlide(this.image, "https://www.nytimes.com/$src")
        favorite.foreground = if (article.isFavorite) {
            ResourcesCompat.getDrawable(resources, R.drawable.fav_filled_icon, null)
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.fav_outline_icon, null)
        }
    }

//    override fun onClick(v: View) {
//        onItemClicked(v, movie)
//    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClicked: (view: View, movie: UserArticle) -> Unit,
            onFavClicked: (view: View, Long, Boolean) -> Unit
        ): ArticleViewHolder {
            val itemBinding = MovieEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return ArticleViewHolder(
                itemBinding,
                onItemClicked,
                onFavClicked
            )
        }
    }
}