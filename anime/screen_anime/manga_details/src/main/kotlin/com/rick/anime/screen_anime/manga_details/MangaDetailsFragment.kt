package com.rick.anime.screen_anime.manga_details

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
import com.rick.anime.screen_anime.manga_details.databinding.AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding
import com.rick.data.model_anime.UserManga
import com.rick.data.ui_components.common.provideGlide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MangaDetailsFragment : Fragment() {

    private var _binding: AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.let {
            val safeArgs = MangaDetailsFragmentArgs.fromBundle(it)
            viewModel.setMangaId(safeArgs.manga)
        }

        binding.toolbar.apply {
            setNavigationIcon(R.drawable.anime_screen_anime_manga_details_ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.bindState(
            viewModel.getManga().asLiveData()
        )

        return binding.root
    }

    private fun AnimeScreenAnimeMangaDetailsFragmentMangaDetailsBinding.bindState(
        mangaLiveData: LiveData<UserManga>,
    ) {

        mangaLiveData.observe(viewLifecycleOwner) { manga ->
            title.text = manga.title

            provideGlide(image, manga.images)

            synopsis.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_synopsis,
                manga.synopsis
            )

            background.text =
                manga.background

            chapters.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_chapters,
                manga.chapters
            )

            volumes.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_volumes,
                manga.volumes
            )

            genreOne.text =
                manga.genres.firstOrNull()?.name
                    ?: getString(R.string.anime_screen_anime_manga_details_anime_screen_anime_no_data)
            genreTwo.text =
                manga.genres.getOrNull(1)?.name
                    ?: getString(R.string.anime_screen_anime_manga_details_anime_screen_anime_no_data)
            genreThree.text =
                manga.genres.getOrNull(2)?.name
                    ?: getString(R.string.anime_screen_anime_manga_details_anime_screen_anime_no_data)

            score.text = getString(
                R.string.anime_screen_anime_manga_details_anime_screen_anime_score,
                manga.score
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
                R.string.anime_screen_anime_manga_details_anime_screen_anime_rank,
                manga.rank
            )

            url.setOnClickListener {
                if (manga.url.isNotEmpty()) {
                    val uri = Uri.parse("com.rick.ecs://anime_common_webviewfragment//$url")
                    findNavController().navigate(uri)
                }
            }

            val series = StringBuilder()
            manga.serializations.forEach {
                series.append(it.name)
                series.append("\n")
            }
            series.deleteAt(series.lastIndex)

            serializationName.text = series.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}