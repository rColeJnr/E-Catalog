package com.rick.screen_anime.details_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.rick.data_anime.model_jikan.Jikan
import com.rick.screen_anime.R
import com.rick.screen_anime.databinding.FragmentJikanDetailsBinding
import com.rick.screen_anime.getListAsString
import com.rick.screen_anime.provideGlide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsJikanFragment : Fragment() {

    private var _binding: FragmentJikanDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsAnimeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJikanDetailsBinding.inflate(inflater, container, false)

        arguments?.let {
            val safeArgs = DetailsJikanFragmentArgs.fromBundle(it)
            viewModel.setJikan(safeArgs.jikan)
        }

        binding.toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.bindState(
            viewModel.jikan
        )

        return binding.root
    }

    private fun FragmentJikanDetailsBinding.bindState(
        jikan: LiveData<Jikan>,
    ) {

        jikan.observe(viewLifecycleOwner) { anmeOrMnga ->
            title.text = anmeOrMnga.title
            provideGlide(image, anmeOrMnga.images.jpg.imageUrl)
            synopsis.text = getString(R.string.synopsis, anmeOrMnga.synopsis)
            background.text = anmeOrMnga.background ?: getString(R.string.no_data)
            genreOne.text =
                anmeOrMnga.genres!!.firstOrNull()?.name ?: getString(R.string.no_data)
            genreTwo.text =
                anmeOrMnga.genres!!.getOrNull(1)?.name ?: getString(R.string.no_data)
            genreThree.text =
                anmeOrMnga.genres!!.getOrNull(2)?.name ?: getString(R.string.no_data)
            score.text = getString(R.string.score, anmeOrMnga.score)
            scoredBy.text = getString(R.string.scored_by, anmeOrMnga.scoredBy)
            rank.text = getString(R.string.rank, anmeOrMnga.rank)
            popularity.text = getString(R.string.popularity, anmeOrMnga.popularity)
            members.text = getString(R.string.members, anmeOrMnga.members)
            favorites.text = getString(R.string.favorites, anmeOrMnga.favorites)
            url.text = getString(R.string.link_web_page) // TODO  anmeOrMnga.url onClick()

            if (anmeOrMnga.type == "TV") {
                airingStatus.text = getString(R.string.airing_status, anmeOrMnga.status)
                aired.text = getString(R.string.aired, anmeOrMnga.aired!!.string)
                episodes.visibility = View.VISIBLE
                episodes.text =
                    getString(R.string.episodes, anmeOrMnga.episodes ?: getString(R.string.no_data))
                runtime.visibility = View.VISIBLE
                runtime.text = getString(R.string.runtime, anmeOrMnga.duration)
                chapters.visibility = View.GONE
                volumes.visibility = View.GONE
                pgRating.text = getString(R.string.pg_rating, anmeOrMnga.rating)
                trailer.visibility = View.VISIBLE
                trailer.text = getString(R.string.link_trailer)  //TODO anmeOrMnga.trailer!!.url
                serializationLink.visibility = View.GONE
                serializationName.visibility = View.GONE
                serializations.visibility = View.GONE
            } else {
                airingStatus.text = getString(R.string.publishing, anmeOrMnga.status)
                aired.text = getString(R.string.published, anmeOrMnga.published?.string)
                episodes.visibility = View.GONE
                runtime.visibility = View.GONE
                chapters.visibility = View.VISIBLE
                chapters.text =
                    getString(R.string.chapters, anmeOrMnga.chapters ?: getString(R.string.no_data))
                volumes.visibility = View.VISIBLE
                volumes.text =
                    getString(R.string.volumes, anmeOrMnga.volumes ?: getString(R.string.no_data))
                trailer.visibility = View.GONE
                pgRating.text =
                    getString(R.string.authors, getListAsString(anmeOrMnga.authors!!.map { it!! }))
                serializationLink.visibility = View.VISIBLE
                serializationLink.text =
                    getString(
                        R.string.serialization_link,
                        anmeOrMnga.serializations!!.firstOrNull()?.url
                    )
                serializationName.visibility = View.VISIBLE
                serializationName.text =
                    getString(
                        R.string.serialization_name,
                        anmeOrMnga.serializations!!.firstOrNull()?.name
                    )
                serializations.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}