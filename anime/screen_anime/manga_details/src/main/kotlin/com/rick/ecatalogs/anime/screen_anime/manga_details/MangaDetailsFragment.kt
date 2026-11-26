package com.rick.ecatalogs.anime.screen_anime.manga_details

import com.rick.ecatalogs.data.ui_components.common.TranslationToggle
import com.rick.ecatalogs.data.translation.TextTranslator
import com.rick.ecatalogs.anime.screen_anime.common.jikanDetailsUri
import androidx.compose.foundation.layout.Column
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import android.net.Uri
import com.rick.ecatalogs.data.ui_design.POSTER_KIND_MANGA
import com.rick.ecatalogs.data.ui_design.posterTransitionName
import androidx.core.view.ViewCompat
import com.google.android.material.transition.MaterialContainerTransform
import android.graphics.Color
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.rick.ecatalogs.anime.screen_anime.common.logMangaWebPageOpened
import com.rick.ecatalogs.anime.screen_anime.common.logScreenView
import com.rick.ecatalogs.anime.screen_anime.manga_details.databinding.AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.anime.data_anime.model.UserManga
import com.rick.ecatalogs.data.ui_components.common.provideGlide
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@AndroidEntryPoint
class MangaDetailsFragment : Fragment() {

    private var _binding: AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaDetailsViewModel by viewModels()
    private val recommendationsViewModel: MangaRecommendationsViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var textTranslator: TextTranslator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            duration = 400L
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding.inflate(
            inflater, container, false
        )

        arguments?.let {
            val safeArgs = MangaDetailsFragmentArgs.fromBundle(it)
            viewModel.setMangaId(safeArgs.manga)
            ViewCompat.setTransitionName(binding.image, posterTransitionName(POSTER_KIND_MANGA, safeArgs.manga))
        }

        // Hold the poster morph until the image is loaded (or a short timeout), so it doesn't
        // land on an empty view.
        postponeEnterTransition()
        binding.root.postDelayed({ if (isAdded) startPostponedEnterTransition() }, MAX_POSTPONE_MILLIS)

        binding.toolbar.apply {
            setNavigationIcon(com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_back_icon)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.bindState(
            viewModel.getManga().asLiveData()
        )

        binding.mangaRecommendations.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                EcsTheme {
                    Column {
                        MangaRecommendationsRow(
                            viewModel = recommendationsViewModel,
                            onClick = { id ->
                                findNavController().navigateWithMotion(
                                    Uri.parse("com.rick.ecs://manga_details_fragment/$id")
                                )
                            },
                            onRelationClick = { relation ->
                                findNavController().navigateWithMotion(
                                    jikanDetailsUri(relation.id, relation.isAnime)
                                )
                            },
                        )
                    }
                }
            }
        }
        analyticsHelper.logScreenView("mangaDetails")

        return binding.root
    }

    private fun AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding.bindState(
        mangaLiveData: LiveData<UserManga>,
    ) {
        val translation = TranslationToggle(showTranslation, listOf(synopsis, background), textTranslator)

        mangaLiveData.observe(viewLifecycleOwner) { manga ->
            title.text = manga.title

            provideGlide(image, manga.images, onReady = { startPostponedEnterTransition() })
            translation.bind(manga.synopsis.trim(), manga.background.trim())

            val publishingText =
                if (manga.publishing) getString(R.string.anime_screen_anime_manga_details_being_published)
                else getString(R.string.anime_screen_anime_manga_details_not_publishing)
            publishing.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_publishing,
                publishingText
            )

            published.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_published,
                manga.published.string
            )

            chapters.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_chapters,
                if (manga.chapters == 0) "n/a" else manga.chapters.toString()
            )

            volumes.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_volumes,
                if (manga.volumes == 0) "n/a" else manga.volumes.toString()
            )

            val themes = StringBuilder()
            manga.themes.forEach {
                themes.append(it.name)
                themes.append("; ")
            }

            this.themes.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_themes,
                themes.toString()
            )

            genreOne.text = manga.genres.firstOrNull()?.name ?: ""
            if (genreOne.text.isNullOrBlank()) genreOne.visibility = View.GONE

            genreTwo.text = manga.genres.getOrNull(1)?.name ?: ""
            if (genreTwo.text.isNullOrBlank()) genreTwo.visibility = View.GONE

            genreThree.text = manga.genres.getOrNull(2)?.name ?: ""
            if (genreThree.text.isNullOrBlank()) genreOne.visibility = View.GONE

            score.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_score, manga.score
            )
            scoredBy.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_scored_by,
                manga.scoredBy
            )
            favorites.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_favorites,
                manga.favorites
            )
            popularity.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_popularity,
                manga.popularity
            )
            rank.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_rank, manga.rank
            )

            val link = manga.url
            this.url.setOnClickListener {
                if (link.isNotEmpty()) {
                    val encodedUrl = URLEncoder.encode(link, StandardCharsets.UTF_8.toString())
                    analyticsHelper.logMangaWebPageOpened(encodedUrl)
                    val uri = Uri.parse("com.rick.ecs://anime_common_webviewfragment/$encodedUrl")
                    findNavController().navigateWithMotion(uri)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

private const val MAX_POSTPONE_MILLIS = 600L
