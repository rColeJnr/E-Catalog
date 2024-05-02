package com.rick.movie.screen_movie.common

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.rick.data.model_movie.UserArticle
import com.rick.movie.screen_movie.common.databinding.MovieScreenMovieCommonDialogArticleDetailsBinding
import com.rick.movie.screen_movie.common.util.provideGlide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleDetailsDialogFragment(
    private val article: UserArticle,
    private val onDialogFavoriteClick: (View, String, Boolean) -> Unit,
    private val onWebUrlClick: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view =
                MovieScreenMovieCommonDialogArticleDetailsBinding.inflate(it.layoutInflater)
            builder.setView(view.root)
            view.closeDialog.setOnClickListener {
                dialog?.dismiss()
            }
            view.favorite.setOnClickListener { view ->
                onDialogFavoriteClick(view, article.id, article.isFavorite)
            }
            view.source.setOnClickListener {
                onWebUrlClick(article.webUrl)
                dialog?.dismiss()
            }
            val image = article.multimedia
            if (image.isNotEmpty()) provideGlide(view.image, "https://www.nytimes.com/$image")
            view.title.text = article.headline
            view.summary.text = article.leadParagraph
            view.date.text =
                resources.getString(
                    R.string.movie_screen_movie_common_release_date,
                    article.pubDate.substring(0, 10)
                )
            view.reporter.text = resources.getString(
                R.string.movie_screen_movie_common_reporter,
                article.byline.original
            )
            view.source.text =
                resources.getString(R.string.movie_screen_movie_common_source, article.source)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}