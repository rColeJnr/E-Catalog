package com.rick.ecatalogs.anime.screen_anime.anime_details

import com.rick.ecatalogs.data.trailer.GalleryItem
import com.rick.ecatalogs.data.trailer.MediaGalleryHost
import com.rick.ecatalogs.data.trailer.TrailerActivity
import com.rick.ecatalogs.data.trailer.youtubeIdFromUrl
import com.rick.ecatalogs.data.ui_components.common.TranslationToggle
import com.rick.ecatalogs.data.translation.TextTranslator
import com.rick.ecatalogs.anime.screen_anime.common.jikanDetailsUri
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri
import com.rick.ecatalogs.data.ui_design.POSTER_KIND_ANIME
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
import com.rick.ecatalogs.anime.screen_anime.common.logAnimeWebPageOpened
import com.rick.ecatalogs.anime.screen_anime.common.logScreenView
import com.rick.ecatalogs.anime.screen_anime.common.logTrailerOpened
import com.rick.ecatalogs.anime.screen_anime.anime_details.databinding.AnimeScreenAnimeAnimeDetailsFragmentAnimeDetailsBinding
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.anime.data_anime.model.UserAnime
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@AndroidEntryPoint
class AnimeDetailsFragment : Fragment() {

    private lateinit var mediaGallery: MediaGalleryHost
    private var _binding: AnimeScreenAnimeAnimeDetailsFragmentAnimeDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnimeDetailsViewModel by viewModels()
    private val extrasViewModel: AnimeExtrasViewModel by viewModels()

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
        _binding = AnimeScreenAnimeAnimeDetailsFragmentAnimeDetailsBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.let {
            val safeArgs = AnimeDetailsFragmentArgs.fromBundle(it)
            viewModel.setAnimeId(safeArgs.anime)
            ViewCompat.setTransitionName(binding.header, posterTransitionName(POSTER_KIND_ANIME, safeArgs.anime))
        }

        // Hold the poster morph until the gallery's first image is loaded (or a short timeout),
        // so it doesn't land on an empty view.
        postponeEnterTransition()
        mediaGallery = MediaGalleryHost(binding.gallery, onFirstImageLoaded = { if (isAdded) startPostponedEnterTransition() })
        binding.root.postDelayed({ if (isAdded) startPostponedEnterTransition() }, MAX_POSTPONE_MILLIS)

        binding.toolbar.apply {
            setNavigationIcon(com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_back_icon)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.bindState(
            viewModel.getAnime().asLiveData()
        )
        binding.animeExtras.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                EcsTheme {
                    AnimeExtrasSection(
                        viewModel = extrasViewModel,
                        onOpenUrl = ::openExternally,
                        onRecommendationClick = { id ->
                            findNavController().navigateWithMotion(
                                Uri.parse("com.rick.ecs://anime_details_fragment/$id")
                            )
                        },
                        onVoiceActorClick = { personId ->
                            findNavController().navigateWithMotion(
                                Uri.parse("com.rick.ecs://jikan_person/$personId")
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
        analyticsHelper.logScreenView("AnimeDetails")

        return binding.root
    }

    private fun AnimeScreenAnimeAnimeDetailsFragmentAnimeDetailsBinding.bindState(
        animeLiveData: LiveData<UserAnime>,
    ) {
        val translation = TranslationToggle(showTranslation, listOf(synopsis, background), textTranslator)

        animeLiveData.observe(viewLifecycleOwner) { anime ->
            title.text = anime.title

            val trailerId = youtubeIdFromUrl(anime.trailer)
            mediaGallery.show(
                items = listOfNotNull(
                    trailerId?.let(GalleryItem::Trailer),
                    anime.images.takeIf { it.isNotBlank() }?.let { GalleryItem.Image(it) },
                ),
                title = anime.title,
            )

            translation.bind(anime.synopsis.trim(), anime.background.trim())

            pgRating.text =
                getString(R.string.anime_screen_anime_anime_details_pg_rating, anime.rating)

            val airing =
                if (anime.airing) getString(R.string.anime_screen_anime_anime_details_airing) else getString(
                    R.string.anime_screen_anime_anime_details_not_airing
                )
            airingStatus.text =
                getString(R.string.anime_screen_anime_anime_details_airing_status, airing)

            aired.text =
                getString(R.string.anime_screen_anime_anime_details_aired, anime.aired)

            episodes.text =
                getString(R.string.anime_screen_anime_anime_details_episodes, anime.episodes)

            runtime.text =
                getString(R.string.anime_screen_anime_anime_details_runtime, anime.runtime)

            genreOne.text =
                anime.genres.firstOrNull()
                    ?: getString(R.string.anime_screen_anime_anime_details_anime_screen_anime_no_data)
            genreTwo.text =
                anime.genres.getOrNull(1)
                    ?: getString(R.string.anime_screen_anime_anime_details_anime_screen_anime_no_data)
            genreThree.text =
                anime.genres.getOrNull(2)
                    ?: getString(R.string.anime_screen_anime_anime_details_anime_screen_anime_no_data)

            score.text = getString(R.string.anime_screen_anime_anime_details_score, anime.score)
            scoredBy.text =
                getString(R.string.anime_screen_anime_anime_details_scored_by, anime.scoredBy)
            favorites.text =
                getString(R.string.anime_screen_anime_anime_details_favorites, anime.favorites)
            popularity.text =
                getString(R.string.anime_screen_anime_anime_details_popularity, anime.popularity)
            rank.text = getString(R.string.anime_screen_anime_anime_details_rank, anime.rank)

            val link = anime.url
            url.setOnClickListener {
                if (link.isNotEmpty()) {
                    val encodedUrl = URLEncoder.encode(link, StandardCharsets.UTF_8.toString())
                    analyticsHelper.logAnimeWebPageOpened(encodedUrl)
                    val uri = Uri.parse("com.rick.ecs://anime_common_webviewfragment/$encodedUrl")
                    findNavController().navigateWithMotion(uri)
                }
            }


            val trailer = anime.trailer
            this.trailer.setOnClickListener {
                if (trailerId != null) {
                    // Plays in the app, on the trailer screen
                    analyticsHelper.logTrailerOpened(trailerId)
                    TrailerActivity.start(requireContext(), trailerId, anime.title)
                } else if (!trailer.isNullOrBlank()) {
                    val encodedUrl = URLEncoder.encode(trailer, StandardCharsets.UTF_8.toString())
                    analyticsHelper.logTrailerOpened(encodedUrl)
                    val uri = Uri.parse("com.rick.ecs://anime_common_webviewfragment/$encodedUrl")
                    findNavController().navigateWithMotion(uri)
                }
            }

        }
    }

    /** Streaming services and trailers open in their app or the browser. */
    private fun openExternally(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            // No app can open it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

private const val MAX_POSTPONE_MILLIS = 600L
