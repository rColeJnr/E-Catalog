package com.rick.ecatalogs.movie.screen_movie.common

import com.rick.ecatalogs.data.ui_components.common.TranslationToggle
import com.rick.ecatalogs.data.translation.TextTranslator
import javax.inject.Inject
import com.rick.ecatalogs.movie.data_movie.data.repository.browse.TmdbBrowseRepository
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.rick.ecatalogs.movie.data_movie.model.UserArticle
import com.rick.ecatalogs.movie.screen_movie.common.databinding.MovieScreenMovieCommonDialogArticleDetailsBinding
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide
import dagger.hilt.android.AndroidEntryPoint

/**
 * An article's details. Its content and callbacks come from the screen that shows it, so when
 * Android recreates it on its own (e.g. after the process was stopped in the background) it has
 * neither and closes: every parameter has a default so the system can still instantiate it.
 */
@AndroidEntryPoint
class ArticleDetailsDialogFragment(
    private val article: UserArticle? = null,
    private val onDialogFavoriteClick: (View, String, Boolean) -> Unit = { _, _, _ -> },
    private val onWebUrlClick: (String) -> Unit = {},
    /** Opens a TMDB movie; when null, "See the film" isn't offered. */
    private val onFilmClick: ((Int) -> Unit)? = null,
) : DialogFragment() {

    @Inject
    lateinit var browseRepository: TmdbBrowseRepository

    @Inject
    lateinit var textTranslator: TextTranslator

    override fun onStart() {
        super.onStart()
        if (article == null) dismissAllowingStateLoss() // recreated by the system, see above
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val article = article ?: return super.onCreateDialog(savedInstanceState)
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = MovieScreenMovieCommonDialogArticleDetailsBinding
                .inflate(it.layoutInflater)
            builder.setView(view.root)
            view.closeDialog.setOnClickListener {
                dialog?.dismiss()
            }
            val filmTitle = filmTitleFromReviewHeadline(article.headline)
            if (onFilmClick != null && filmTitle != null) {
                val reviewYear = article.pubDate.take(4).toIntOrNull()
                view.filmLink.setContent {
                    EcsTheme {
                        FilmLinkButton(
                            findFilmId = { browseRepository.findFilm(filmTitle, reviewYear)?.id },
                            onOpenFilm = { id ->
                                dialog?.dismiss()
                                onFilmClick.invoke(id)
                            },
                        )
                    }
                }
            }
            // The dialog keeps its own copy of the state so repeated taps toggle correctly
            var isFavorite = article.isFavorite
            view.favorite.setFavoriteIcon(isFavorite)
            view.favorite.setOnClickListener { fav ->
                onDialogFavoriteClick(fav, article.id, isFavorite)
                isFavorite = !isFavorite
                view.favorite.setFavoriteIcon(isFavorite)
            }
            view.source.setOnClickListener {
                onWebUrlClick(article.webUrl)
                dialog?.dismiss()
            }
            val image = article.multimedia
            if (image.isNotEmpty()) provideGlide(view.image, image)
            view.title.text = article.headline
            TranslationToggle(view.showTranslation, view.summary, textTranslator)
                .bind(article.leadParagraph)
            view.date.text =
                resources.getString(
                    R.string.movie_screen_movie_common_release_date,
                    article.pubDate.take(10) // "2026-11-21T10:00:05+0000" -> the date
                )
            view.reporter.text = resources.getString(
                R.string.movie_screen_movie_common_reporter,
                article.byline.original
            )
            view.source.text =
                resources.getString(
                    R.string.movie_screen_movie_common_source,
                    article.source
                )

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

private fun android.widget.ImageView.setFavoriteIcon(isFavorite: Boolean) = setImageResource(
    if (isFavorite) {
        com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_filled
    } else {
        com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_outlined
    }
)
