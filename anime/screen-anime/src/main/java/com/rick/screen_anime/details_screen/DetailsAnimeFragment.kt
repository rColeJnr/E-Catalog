package com.rick.screen_anime.details_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.rick.data_anime.model_anime.Anime
import com.rick.data_anime.model_manga.Manga
import com.rick.screen_anime.R
import com.rick.screen_anime.databinding.FragmentDetailsAnimeBinding
import com.rick.screen_anime.manga_screen.getListAsString
import com.rick.screen_anime.manga_screen.provideGlide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsAnimeFragment : Fragment() {

    private var _binding: FragmentDetailsAnimeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsAnimeViewModel by viewModels()

    private var anime: Anime? = null
    private var manga: Manga? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsAnimeBinding.inflate(inflater, container, false)

        arguments?.let {
            val safeArgs = DetailsAnimeFragmentArgs.fromBundle(it)
            anime = safeArgs.anime
            manga = safeArgs.manga
        }

        anime?.let {
            viewModel.setAnimaValue(it)
        } ?: manga?.let {
            viewModel.setMangaValue(it)
        }

        binding.toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
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

    private fun FragmentDetailsAnimeBinding.bindState(
        anime: LiveData<Anime>,
        manga: LiveData<Manga>
    ) {

        anime.observe(viewLifecycleOwner) { anme ->
            title.text = anme.title
            anme.images.jpg.imageUrl?.let { provideGlide(image, it) }
            titleJapanese.text = getString(R.string.title_japanese, anme.titleJapanese)
            synopsis.text = getString(R.string.synopsis)
            airingStatus.text = getString(R.string.airing_status, anme.airingStatus)
            aired.text = getString(R.string.aired, anme.aired.string)
            episodes.visibility = View.VISIBLE
            episodes.text = getString(R.string.episodes, anme.episodes)
            runtime.visibility = View.VISIBLE
            runtime.text = getString(R.string.runtime, anme.duration)
            chapters.visibility = View.GONE
            volumes.visibility = View.GONE
            genreOne.text = anme.genres.firstOrNull()?.name ?: ""
            genreTwo.text = anme.genres.getOrNull(1)?.name ?: ""
            genreThree.text = anme.genres.getOrNull(2)?.name ?: ""
            pgRating.visibility = View.VISIBLE
            pgRating.text = getString(R.string.pg_rating, anme.rating)
            score.text = getString(R.string.score, anme.score)
            scoredBy.text = getString(R.string.scored_by, anme.scoredBy)
            rank.text = getString(R.string.rank, anme.rank)
            popularity.text = getString(R.string.popularity, anme.popularity)
            members.text = getString(R.string.members, anme.members)
            favorites.text = getString(R.string.favorites, anme.favorites)
            trailer.visibility = View.VISIBLE
            trailer.text = getString(R.string.link_trailer, anme.trailer.url)
            authors.visibility = View.GONE
            serializationLink.visibility = View.GONE
            serializationName.visibility = View.GONE
            serializations.visibility = View.GONE
        }

        manga.observe(viewLifecycleOwner) { mnga ->
            title.text = mnga.title
            mnga.imagesDto.jpg.imageUrl?.let { provideGlide(image, it) }
            titleJapanese.text = getString(R.string.title_japanese, mnga.titleJapanese)
            synopsis.text = getString(R.string.synopsis)
            airingStatus.text = getString(R.string.airing_status, mnga.status)
            aired.text = getString(R.string.aired, mnga.published.string)
            episodes.visibility = View.GONE
            runtime.visibility = View.GONE
            chapters.visibility = View.VISIBLE
            chapters.text = getString(R.string.chapters, mnga.chapters)
            volumes.visibility = View.VISIBLE
            volumes.text = getString(R.string.volumes, mnga.volumes)
            genreOne.text = mnga.genres.firstOrNull()?.name ?: ""
            genreTwo.text = mnga.genres.getOrNull(1)?.name ?: ""
            genreThree.text = mnga.genres.getOrNull(2)?.name ?: ""
            pgRating.visibility = View.GONE
            score.text = getString(R.string.score, mnga.score)
            scoredBy.text = getString(R.string.scored_by, mnga.scoredBy)
            rank.text = getString(R.string.rank, mnga.rank)
            popularity.text = getString(R.string.popularity, mnga.popularity)
            members.text = getString(R.string.members, mnga.members)
            favorites.text = getString(R.string.favorites, mnga.favorites)
            trailer.visibility = View.GONE
            authors.visibility = View.VISIBLE
            authors.text = getString(R.string.authors, getListAsString(mnga.authors))
            serializationLink.visibility = View.VISIBLE
            serializationLink.text =
                getString(R.string.serialization_link, mnga.serializations.firstOrNull()?.url)
            serializationName.visibility = View.VISIBLE
            serializationName.text =
                getString(R.string.serialization_link, mnga.serializations.firstOrNull()?.name)
            serializations.visibility = View.VISIBLE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}