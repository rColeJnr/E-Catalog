package com.rick.screen_anime.details_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.rick.data.model_anime.UserAnime
import com.rick.data.model_anime.UserManga
import com.rick.screen_anime.R
import com.rick.screen_anime.databinding.AnimeScreenAnimeFragmentJikanDetailsBinding
import com.rick.screen_anime.provideGlide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsJikanFragment : Fragment() {

    private var _binding: AnimeScreenAnimeFragmentJikanDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsAnimeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AnimeScreenAnimeFragmentJikanDetailsBinding.inflate(inflater, container, false)

//        arguments?.let {
//            val safeArgs = DetailsJikanFragmentArgs.fromBundle(it)
////            viewModel.setAnime(safeArgs.jikan)
////            viewModel.setManga(safeArgs.jikan)
//        }

        binding.toolbar.apply {
            setNavigationIcon(R.drawable.anime_screen_anime_ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.bindState(
            viewModel.anime,
            viewModel.manga
        )

        return binding.root
    }

    private fun AnimeScreenAnimeFragmentJikanDetailsBinding.bindState(
        anime: LiveData<UserAnime>,
        manga: LiveData<UserManga>,
    ) {

        anime.observe(viewLifecycleOwner) { anmeOrMnga ->
            title.text = anmeOrMnga.title
            provideGlide(image, anmeOrMnga.background)
            synopsis.text = getString(R.string.anime_screen_anime_synopsis, anmeOrMnga.synopsis)
            background.text =
                anmeOrMnga.background ?: getString(R.string.anime_screen_anime_no_data)
            genreOne.text =
                anmeOrMnga.genres!!.firstOrNull()?.name
                    ?: getString(R.string.anime_screen_anime_no_data)
            genreTwo.text =
                anmeOrMnga.genres!!.getOrNull(1)?.name
                    ?: getString(R.string.anime_screen_anime_no_data)
            genreThree.text =
                anmeOrMnga.genres!!.getOrNull(2)?.name
                    ?: getString(R.string.anime_screen_anime_no_data)
            score.text = getString(R.string.anime_screen_anime_score, anmeOrMnga.score)
            scoredBy.text = getString(R.string.anime_screen_anime_scored_by, anmeOrMnga.scoredBy)
            rank.text = getString(R.string.anime_screen_anime_rank, anmeOrMnga.rank)
            popularity.text =
                getString(R.string.anime_screen_anime_popularity, anmeOrMnga.popularity)
//            favorites.text = getString(R.string.anime_screen_anime_favorite, anmeOrMnga.favorites)
            url.text =
                getString(R.string.anime_screen_anime_link_web_page) // TODO  anmeOrMnga.url onClick()

//      onClick      if (anmeOrMnga.type == "TV") {
//                airingStatus.text = getString(R.string.airing_status, anmeOrMnga.status)
//                aired.text = getString(R.string.aired, anmeOrMnga.aired!!.string)
//                episodes.visibility = View.VISIBLE
//                episodes.text =
//                    getString(R.string.episodes, anmeOrMnga.episodes ?: getString(R.string.no_data))
//                runtime.visibility = View.VISIBLE
//                runtime.text = getString(R.string.runtime, anmeOrMnga.duration)
//                chapters.visibility = View.GONE
//                volumes.visibility = View.GONE
//                pgRating.text = getString(R.string.pg_rating, anmeOrMnga.rating)
//                trailer.visibility = View.VISIBLE
//                trailer.text = getString(R.string.link_trailer)  //TODO anmeOrMnga.trailer!!.url
//                serializationLink.visibility = View.GONE
//                serializationName.visibility = View.GONE
//                serializations.visibility = View.GONE
//            } else {
//                airingStatus.text = getString(R.string.publishing, anmeOrMnga.status)
//                aired.text = getString(R.string.published, anmeOrMnga.published?.string)
//                episodes.visibility = View.GONE
//                runtime.visibility = View.GONE
//                chapters.visibility = View.VISIBLE
//                chapters.text =
//                    getString(R.string.chapters, anmeOrMnga.chapters ?: getString(R.string.no_data))
//                volumes.visibility = View.VISIBLE
//                volumes.text =
//                    getString(R.string.volumes, anmeOrMnga.volumes ?: getString(R.string.no_data))
//                trailer.visibility = View.GONE
//                pgRating.text =
//                    getString(R.string.authors, getListAsString(anmeOrMnga.authors!!.map { it!! }))
//                serializationLink.visibility = View.VISIBLE
//                serializationLink.text =
//                    getString(
//                        R.string.serialization_link,
//                        anmeOrMnga.serializations!!.firstOrNull()?.url
//                    )
//                serializationName.visibility = View.VISIBLE
//                serializationName.text =
//                    getString(
//                        R.string.serialization_name,
//                        anmeOrMnga.serializations!!.firstOrNull()?.name
//                    )
//                serializations.visibility = View.VISIBLE
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}