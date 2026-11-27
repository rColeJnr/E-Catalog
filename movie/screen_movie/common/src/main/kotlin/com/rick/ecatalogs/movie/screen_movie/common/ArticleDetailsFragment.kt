package com.rick.ecatalogs.movie.screen_movie.common

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.data.translation.TextTranslator
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import com.rick.ecatalogs.data.ui_components.common.TranslationToggle
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import com.rick.ecatalogs.movie.data_movie.data.repository.browse.TmdbBrowseRepository
import com.rick.ecatalogs.movie.data_movie.model.UserArticle
import com.rick.ecatalogs.movie.screen_movie.common.databinding.MovieScreenMovieCommonFragmentArticleDetailsBinding
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import javax.inject.Inject
import com.rick.ecatalogs.data.ui_design.R as DesignR

/**
 * A movie review (NYT) on its own screen, in the bestseller details screen's style: picture, source,
 * headline, critic and date; the summary and the review's opening (translatable); the whole review
 * in the in-app browser; and the film it's about, found on TMDB. The favorite star is in the toolbar.
 * Opened with [articleDetailsUri] from the catalog, search and favorites.
 */
@AndroidEntryPoint
class ArticleDetailsFragment : Fragment() {

    private var _binding: MovieScreenMovieCommonFragmentArticleDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArticleDetailsViewModel by viewModels()

    @Inject
    lateinit var browseRepository: TmdbBrowseRepository

    @Inject
    lateinit var textTranslator: TextTranslator

    private var translation: TranslationToggle? = null
    private var filmLinkFor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val duration = resources.getInteger(DesignR.integer.data_ui_design_motion_duration_medium).toLong()
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).setDuration(duration)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).setDuration(duration)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MovieScreenMovieCommonFragmentArticleDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.articleDetailsToolbar.apply {
            setNavigationIcon(DesignR.drawable.data_ui_design_back_icon)
            setNavigationOnClickListener { findNavController().navigateUp() }
            inflateMenu(R.menu.movie_screen_movie_common_article_details_menu)
            setOnMenuItemClickListener {
                viewModel.toggleFavorite()
                true
            }
        }
        // NYT reviews are English: the summary and the opening translate together
        translation = TranslationToggle(
            binding.articleDetailsTranslate,
            listOf(binding.articleDetailsAbstract, binding.articleDetailsLead),
            textTranslator,
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::bind)
            }
        }
    }

    private fun bind(state: ArticleDetailsUiState) = with(binding) {
        articleDetailsUnavailable.isVisible = state is ArticleDetailsUiState.Unavailable
        val article = (state as? ArticleDetailsUiState.Success)?.article
        articleDetailsContent.visibility = if (article != null) View.VISIBLE else View.INVISIBLE
        if (article == null) return@with

        articleDetailsImageCard.isVisible = article.multimedia.isNotBlank()
        if (article.multimedia.isNotBlank()) {
            articleDetailsImage.contentDescription =
                getString(R.string.movie_screen_movie_common_article_image, article.headline)
            provideGlide(articleDetailsImage, article.multimedia)
        }
        articleDetailsSource.text = article.source
        articleDetailsSource.isVisible = article.source.isNotBlank()
        articleDetailsHeadline.text = article.headline
        articleDetailsByline.text = article.byline.original
        articleDetailsByline.isVisible = article.byline.original.isNotBlank()
        articleDetailsDate.text = publicationDate(article.pubDate)

        val abstract = article.abstract.trim()
        val lead = article.leadParagraph.trim()
        // NYT sometimes repeats the opening as the abstract
        val showAbstract = abstract.isNotBlank() && abstract != lead
        articleDetailsAbstractSection.isVisible = showAbstract
        translation?.bind(if (showAbstract) abstract else "", lead)

        articleDetailsRead.isVisible = article.webUrl.isNotBlank()
        articleDetailsRead.setOnClickListener { openInApp(article.webUrl) }

        bindFavorite(article.isFavorite)
        bindFilmLink(article)
    }

    private fun bindFavorite(isFavorite: Boolean) {
        binding.articleDetailsToolbar.menu.findItem(R.id.article_details_favorite)?.apply {
            setIcon(
                if (isFavorite) DesignR.drawable.data_ui_design_favorite_filled
                else DesignR.drawable.data_ui_design_favorite_outlined
            )
            title = getString(
                if (isFavorite) R.string.movie_screen_movie_common_article_remove_favorite
                else R.string.movie_screen_movie_common_article_add_favorite
            )
        }
    }

    /** "See the film" when the headline names one ("‘Title’ Review: …"), looked up once. */
    private fun bindFilmLink(article: UserArticle) {
        val filmTitle = filmTitleFromReviewHeadline(article.headline)
        binding.articleDetailsFilmSection.isVisible = filmTitle != null
        if (filmTitle == null || filmTitle == filmLinkFor) return
        filmLinkFor = filmTitle
        val reviewYear = article.pubDate.take(4).toIntOrNull()
        binding.articleDetailsFilm.setContent {
            EcsTheme {
                FilmLinkButton(
                    findFilmId = { browseRepository.findFilm(filmTitle, reviewYear)?.id },
                    onOpenFilm = { id ->
                        findNavController().navigateWithMotion(
                            Uri.parse("com.rick.ecs://trending_movie_details_fragment/$id")
                        )
                    },
                )
            }
        }
    }

    private fun openInApp(url: String) {
        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
        findNavController().navigateWithMotion(Uri.parse("com.rick.ecs://movie_common_webviewfragment/$encodedUrl"))
    }

    /** "2026-11-21T10:00:05+0000" → the date in the phone's format ("21 Nov 2026"). */
    private fun publicationDate(pubDate: String): String {
        val day = pubDate.take(10)
        return try {
            LocalDate.parse(day).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        } catch (e: DateTimeParseException) {
            day
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        translation = null
        filmLinkFor = null
        _binding = null
    }
}

/** Deep link to a review's details screen (any stored article: catalog, search or favorite). */
fun articleDetailsUri(articleId: String): Uri =
    Uri.parse("com.rick.ecs://movie_common_article_details/${Uri.encode(articleId)}")
