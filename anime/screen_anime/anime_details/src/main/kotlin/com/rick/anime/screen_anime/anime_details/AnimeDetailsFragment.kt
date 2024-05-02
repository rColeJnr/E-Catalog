package com.rick.anime.screen_anime.anime_details

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.rick.anime.anime_screen.common.logAnimeWebPageOpened
import com.rick.anime.anime_screen.common.logScreenView
import com.rick.anime.anime_screen.common.logTrailerOpened
import com.rick.anime.screen_anime.anime_details.databinding.AnimeScreenAnimeAnimeDetailsFragmentAnimeDetailsBinding
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_anime.UserAnime
import com.rick.data.ui_components.common.provideGlide
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@AndroidEntryPoint
class AnimeDetailsFragment : Fragment() {

    private var _binding: AnimeScreenAnimeAnimeDetailsFragmentAnimeDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnimeDetailsViewModel by viewModels()


    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

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
        }

        binding.toolbar.apply {
            setNavigationIcon(R.drawable.anime_screen_anime_anime_details_ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.bindState(
            viewModel.getAnime().asLiveData()
        )
        analyticsHelper.logScreenView("AnimeDetails")

        return binding.root
    }

    private fun AnimeScreenAnimeAnimeDetailsFragmentAnimeDetailsBinding.bindState(
        animeLiveData: LiveData<UserAnime>,
    ) {

        animeLiveData.observe(viewLifecycleOwner) { anime ->
            title.text = anime.title

            provideGlide(image, anime.images)

            synopsis.text =
                getString(R.string.anime_screen_anime_anime_details_synopsis, anime.synopsis)

            background.text = anime.background

            pgRating.text =
                getString(R.string.anime_screen_anime_anime_details_pg_rating, anime.rating)

            val airing = if (anime.airing) "Aring!" else "Not airing!"
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
                    findNavController().navigate(uri)
                }
            }


            val trailer = anime.trailer
            this.trailer.setOnClickListener {
                if (!trailer.isNullOrBlank()) {
                    val encodedUrl = URLEncoder.encode(trailer, StandardCharsets.UTF_8.toString())
                    analyticsHelper.logTrailerOpened(encodedUrl)
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