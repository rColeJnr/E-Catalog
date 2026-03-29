package com.rick.anime.screen_anime.manga_details

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rick.anime.anime_screen.common.TranslationEvent
import com.rick.anime.anime_screen.common.TranslationViewModel
import com.rick.anime.anime_screen.common.logMangaWebPageOpened
import com.rick.anime.anime_screen.common.logScreenView
import com.rick.anime.screen_anime.manga_details.databinding.AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_anime.UserManga
import com.rick.data.ui_components.common.provideGlide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MangaDetailsFragment : Fragment() {

    private var _binding: AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaDetailsViewModel by viewModels()
    private val translationViewModel: TranslationViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding.inflate(
            inflater, container, false
        )

        arguments?.let {
            val safeArgs = MangaDetailsFragmentArgs.fromBundle(it)
            viewModel.setMangaId(safeArgs.manga)
        }

        if (translationViewModel.location.value.isEmpty()) {
            translationViewModel.setLocation(Locale.getDefault().language)
        }

        binding.toolbar.apply {
            setNavigationIcon(com.rick.data.ui_design.R.drawable.data_ui_design_back_icon)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.bindState(
            viewModel.getManga().asLiveData()
        )

        analyticsHelper.logScreenView("mangaDetails")

        return binding.root
    }

    private fun AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding.bindState(
        mangaLiveData: LiveData<UserManga>,
    ) {

        mangaLiveData.observe(viewLifecycleOwner) { manga ->
            title.text = manga.title

            provideGlide(image, manga.images)

            if (translationViewModel.location.value == "en") {
                showTranslation.visibility = View.GONE
            } else {
                showTranslation.setOnClickListener {
                    if (showTranslation.text == getString(R.string.anime_screen_anime_manga_details_show_original)) {
                        synopsis.animate().alpha(0f).setDuration(200).withEndAction {
                            synopsis.text = manga.synopsis
                            synopsis.animate().alpha(1f).setDuration(200).start()
                        }.start()

                        background.animate().alpha(0f).setDuration(200).withEndAction {
                            background.text = manga.background
                            showTranslation.animate().alpha(0f).setDuration(200).withEndAction {
                                showTranslation.text =
                                    getString(R.string.anime_screen_anime_manga_details_show_translation)
                                showTranslation.animate().alpha(1f).setDuration(200).start()
                            }.start()
                            background.animate().alpha(1f).setDuration(200).start()
                        }.start()
                    } else {
                        translationViewModel.onEvent(
                            TranslationEvent.GetTranslation(
                                listOf(manga.synopsis, manga.background),
                                translationViewModel.location.value
                            )
                        )
                        lifecycleScope.launch {
                            translationViewModel.translation.collectLatest { translations ->
                                if (translations.isNotEmpty()) {
                                    synopsis.animate().alpha(0f).setDuration(200).withEndAction {
                                        synopsis.text = translations.first().text
                                        synopsis.animate().alpha(1f).setDuration(200).start()
                                    }.start()

                                    background.animate().alpha(0f).setDuration(200).withEndAction {
                                        background.text = translations.last().text
                                        showTranslation.animate().alpha(0f).setDuration(200)
                                            .withEndAction {
                                                showTranslation.text = getString(
                                                    R.string.anime_screen_anime_manga_details_show_original
                                                )
                                                showTranslation.animate().alpha(1f).setDuration(200)
                                                    .start()
                                            }.start()
                                        background.animate().alpha(1f).setDuration(200).start()
                                    }.start()
                                }
                            }
                        }
                    }
                }
            }
            synopsis.text = manga.synopsis.trim()

            background.text = manga.background.trim()

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
                    findNavController().navigate(uri)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}