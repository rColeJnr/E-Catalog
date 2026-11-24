package com.rick.ecatalogs.movie.screen_movie.article_catalog

import androidx.core.view.isVisible
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.movie.data_movie.model.UserArticle
import com.rick.ecatalogs.movie.screen_movie.article_catalog.databinding.MovieScreenMovieArticleCatalogMovieEntryBinding
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide

class ArticleViewHolder(
    binding: MovieScreenMovieArticleCatalogMovieEntryBinding,
    private val onItemClicked: (UserArticle) -> Unit,
    private val onFavClicked: (view: View, String, Boolean) -> Unit,
    private val onTranslationClick: (UserArticle, List<String>) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.movieName
    private val image = binding.image
    private val summary = binding.movieSummary
    private val favorite = binding.favorite
    private val date = binding.articleDate
    private val byline = binding.articleByline
    private val showTranslation = binding.showTranslation

    private lateinit var article: UserArticle

    init {
        binding.root.setOnClickListener {
            onItemClicked(article)
        }
        favorite.setOnClickListener {
            onFavClicked(it, article.id, article.isFavorite)
        }
        showTranslation.setOnClickListener {
            onTranslationClick(article, listOf(article.leadParagraph))
        }
    }

    fun bind(article: UserArticle) {
        this.article = article
        this.title.text = article.headline
        this.summary.text = article.leadParagraph
        val src = article.multimedia
        if (src.isNotBlank()) provideGlide(this.image, src)
        favorite.setImageResource(
            if (article.isFavorite) {
                com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_filled
            } else {
                com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_outlined
            }
        )
        val reviewDate = formatPubDate(article.pubDate)
        date.text = reviewDate
        date.isVisible = reviewDate.isNotEmpty()
        val reviewer = article.byline.original
        byline.text = reviewer
        byline.isVisible = reviewer.isNotBlank()
    }

    /** "2026-11-21T10:00:05+0000" -> "Nov 21, 2026" in the user's locale; empty if unparseable. */
    private fun formatPubDate(pubDate: String): String = try {
        LocalDate.parse(pubDate.take(10))
            .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    } catch (e: DateTimeParseException) {
        ""
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClicked: (UserArticle) -> Unit,
            onFavClicked: (view: View, String, Boolean) -> Unit,
            onTranslationClick: (UserArticle, List<String>) -> Unit
        ): ArticleViewHolder {
            val itemBinding = MovieScreenMovieArticleCatalogMovieEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return ArticleViewHolder(
                itemBinding,
                onItemClicked,
                onFavClicked,
                onTranslationClick
            )
        }
    }
}