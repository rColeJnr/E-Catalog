package com.rick.screen_movie.trendingseries_details_screen

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentTvDetailsBinding
import com.rick.screen_movie.trendingmovie_details_screen.SimilarDetailsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrendingSeriesDetailsFragment : Fragment() {

    private var _binding: FragmentTvDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TvDetailsViewModel by viewModels()

    private lateinit var similarsAdapter: SimilarDetailsAdapter
    private var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvDetailsBinding.inflate(inflater, container, false)

        arguments?.let {
            val safeArgs = TrendingSeriesDetailsFragmentArgs.fromBundle(it)
            id = safeArgs.tv
        }

        viewModel.onEvent(TvDetailsEvents.GetTv(id!!))

//        binding.toolbar.apply {
//            setNavigationIcon(R.drawable.ic_arrow_back)
//            setNavigationOnClickListener {
//                findNavController().navigateUp()
//            }
//        }

        binding.bindState(
            tv = viewModel.series,
            loading = viewModel.searchLoading,
            error = viewModel.searchError
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailsCardView.transitionName = getString(R.string.movie_detail_transition_name)
        binding.root.transitionName = getString(R.string.search_transition_name, id.toString())
    }

    private fun FragmentTvDetailsBinding.bindState(
        tv: LiveData<TvDetailsUiState.Tv>,
        loading: LiveData<TvDetailsUiState.Loading>,
        error: LiveData<TvDetailsUiState.Error>
    ) {
//        val layoutManager = LinearLayoutManager(
//            requireContext(),
//            LinearLayoutManager.HORIZONTAL,
//            false
//        )
//        val layoutManager2 = LinearLayoutManager(
//            requireContext(),
//            LinearLayoutManager.HORIZONTAL,
//            false
//        )
        val similarLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        similarsAdapter = SimilarDetailsAdapter()
        listSimilars.layoutManager = similarLayoutManager
        listSimilars.adapter = similarsAdapter

        bindList(
            similarsAdapter, tv, loading, error
        )
    }

    private fun FragmentTvDetailsBinding.bindList(
        similarDetailsAdapter: SimilarDetailsAdapter,
        tv: LiveData<TvDetailsUiState.Tv>,
        loading: LiveData<TvDetailsUiState.Loading>,
        error: LiveData<TvDetailsUiState.Error>
    ) {

        val noData = getString(R.string.no_data)

        /* tv.observe(viewLifecycleOwner) { tmdb ->
             val trendingTv = tmdb.tv
             tvTitle.text = trendingTv.name
             if (trendingTv.image.isNotEmpty()) {
                 provideGlide(image, trendingTv.image)
             }
             summary.text = trendingTv.summary
             adult.text = resources.getString(
                 R.string.adult_content,
                 if (trendingTv.adult) "True" else "False"
             )
             genres.text =
                 resources.getString(R.string.genres, stringFromList(trendingTv.genres))

             firstAirDate.text = resources.getString(                R.string.first_air_date, trendingTv.firstAirDate            )
             inProduction.text = resources.getString(R.string.in_production, if (trendingTv.inProduction) "true" else "false")

             lastAirDate.text = resources.getString(R.string.last_air_date, trendingTv.lastAirDate)

             creator.text = resources.getString(R.string.creators, stringFromList(trendingTv.createdBy))

             numberOfSeasons.text = getString(R.string.number_of_seasons, trendingTv.numberOfSeasons)

             imdbChip.text =
                 resources.getString(
                     R.string.imdb_rating, trendingTv.popularity.toString()
                 )
             movieDbChip.text = resources.getString(R.string.db_rating, trendingTv.voteAverage)

             similarDetailsAdapter.similarsDiffer.submitList(trendingTv.similar.results.map { it.toSimilar() })
         }
 */
        loading.observe(viewLifecycleOwner) { progressing ->
            if (progressing.loading) {
                detailsProgressBar.visibility = View.VISIBLE
            } else detailsProgressBar.visibility = View.GONE
        }

        error.observe(viewLifecycleOwner) { msg ->
            if (msg.msg.isNullOrBlank()) detailsErrorMessage.visibility = View.GONE
            else detailsErrorMessage.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
