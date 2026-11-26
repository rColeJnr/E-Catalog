package com.rick.ecatalogs.movie.screen_movie.trending_series_catalog

import com.rick.ecatalogs.data.translation.TextTranslator
import com.rick.ecatalogs.data.ui_design.animateItemsOnScroll
import com.rick.ecatalogs.movie.screen_movie.common.R as CommonR
import kotlinx.coroutines.flow.combine
import com.rick.ecatalogs.movie.screen_movie.common.BrowseRow
import com.rick.ecatalogs.movie.screen_movie.common.BrowseItem
import com.rick.ecatalogs.movie.screen_movie.common.BrowseHeaderState
import com.rick.ecatalogs.movie.screen_movie.common.BrowseHeaderAdapter
import com.rick.ecatalogs.movie.screen_movie.common.BrowseGenre
import androidx.recyclerview.widget.ConcatAdapter
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import android.animation.AnimatorInflater
import com.rick.ecatalogs.data.ui_design.navigateWithSharedElement
import com.google.android.material.transition.MaterialElevationScale
import androidx.core.view.doOnPreDraw
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.imageview.ShapeableImageView
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.movie.data_movie.model.UserTrendingSeries
import com.rick.ecatalogs.movie.screen_movie.common.RemotePresentationState
import com.rick.ecatalogs.movie.screen_movie.common.asRemotePresentationState
import com.rick.ecatalogs.movie.screen_movie.common.logScreenView
import com.rick.ecatalogs.movie.screen_movie.common.logTrendingSeriesOpened
import com.rick.ecatalogs.movie.screen_movie.trending_series_catalog.databinding.MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class TrendingSeriesFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: TrendingSeriesViewModel by viewModels()

    private lateinit var adapter: TrendingSeriesAdapter
    private lateinit var headerAdapter: BrowseHeaderAdapter
    private lateinit var navController: NavController

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var textTranslator: TextTranslator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding.inflate(
                inflater,
                container,
                false
            )

        navController = findNavController()

        initAdapter()

        binding.bindList(
            viewModel.pagingDataFlow,
            adapter = adapter
        )

        analyticsHelper.logScreenView("trendingMovieCatalog")

        // Wait for the list to lay out so the return transition finds the poster
        postponeEnterTransition()
        binding.root.doOnPreDraw { startPostponedEnterTransition() }
        return binding.root
    }

    private fun initAdapter() {
        adapter =
            TrendingSeriesAdapter(
                this::onSeriesClick,
                this::onFavClick,
                textTranslator,
            )
        binding.recyclerView.itemAnimator = DefaultItemAnimator().apply {
            // Rebinding the same item shouldn't fade the whole card (looked like flicker)
            supportsChangeAnimations = false
        }
        headerAdapter = BrowseHeaderAdapter(
            title = getString(R.string.movie_screen_movie_trending_series_catalog_header),
            slogan = getString(R.string.movie_screen_movie_trending_series_catalog_slogan),
            allGenresLabel = getString(CommonR.string.movie_screen_movie_common_browse_all),
            onItemClick = { item ->
                findNavController().navigateWithMotion(
                    Uri.parse("com.rick.ecs://trending_series_details_fragment/${item.id}")
                )
            },
            onBellClick = {},
            onGenreSelected = { genreId ->
                viewModel.selectGenre(genreId)
                binding.recyclerView.scrollToPosition(0)
            },
        )
        // Cards fade, rise and scale in as they scroll into view
        binding.recyclerView.animateItemsOnScroll()
        binding.recyclerView.adapter = ConcatAdapter(headerAdapter, adapter)
        bindHeader()
    }

    /** "Airing today" and the genre chips. */
    private fun bindHeader() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.airingToday,
                    viewModel.genres,
                    viewModel.selectedGenre,
                ) { airingToday, genres, selected ->
                    BrowseHeaderState(
                        rows = listOf(
                            BrowseRow(
                                getString(CommonR.string.movie_screen_movie_common_airing_today),
                                airingToday.map { series ->
                                    BrowseItem(
                                        id = series.id,
                                        title = series.name,
                                        posterPath = series.image?.ifBlank { null },
                                        subtitle = series.voteAverage.takeIf { it > 0 }?.let {
                                            getString(CommonR.string.movie_screen_movie_common_rating, it)
                                        },
                                    )
                                },
                            ),
                        ),
                        genres = genres.map { BrowseGenre(it.id, it.name) },
                        selectedGenreId = selected,
                    )
                }.collect { headerAdapter.state = it }
            }
        }
    }

    private fun MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding.bindList(
        pagingDataFlow: Flow<PagingData<UserTrendingSeries>>,
        adapter: TrendingSeriesAdapter
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            pagingDataFlow.collectLatest(adapter::submitData)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                // show progress bar during initial load or refresh.
                // Genre lists come straight from the network (no mediator)
                loadingIndicator.isVisible =
                    (loadState.mediator?.refresh ?: loadState.source.refresh) is LoadState.Loading
                // show empty list.
                // Only once the stored list has been read too, so a cached screen doesn't flash
                // "No results" before its items appear
                emptyList.isVisible = !loadingIndicator.isVisible &&
                    loadState.source.refresh is LoadState.NotLoading &&
                    adapter.itemCount == 0

                val errorState = loadState.source.refresh as? LoadState.Error
                    ?: loadState.mediator?.refresh as? LoadState.Error

                errorState?.let {
                    android.widget.Toast.makeText(
                        context,
                        getString(com.rick.ecatalogs.data.ui_components.common.R.string.data_ui_components_common_load_failed),
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val notLoading = adapter.loadStateFlow.asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        viewLifecycleOwner.lifecycleScope.launch {
            notLoading.collectLatest {
                if (it) recyclerView.scrollToPosition(0)
            }
        }

        // No pull-to-refresh (content changes weekly): tap the empty / error message to retry
        emptyList.setOnClickListener {
            adapter.refresh()
        }
    }

    private fun onSeriesClick(poster: View, id: Int) {
        val motionDuration = resources
            .getInteger(com.rick.ecatalogs.data.ui_design.R.integer.data_ui_design_motion_duration_long)
            .toLong()
        // The list fades back a little while the poster morphs into the details screen
        exitTransition = MaterialElevationScale(false).apply { duration = motionDuration }
        reenterTransition = MaterialElevationScale(true).apply { duration = motionDuration }
        analyticsHelper.logTrendingSeriesOpened(id.toString())
        val uri = Uri.parse("com.rick.ecs://trending_series_details_fragment/$id")
        findNavController().navigateWithSharedElement(uri, poster)
    }

    private fun onFavClick(view: View, id: Int, isFavorite: Boolean) {
        val set = AnimatorInflater.loadAnimator(
            requireContext(),
            com.rick.ecatalogs.movie.screen_movie.common.R.animator.movie_screen_movie_common_animator
        ) as AnimatorSet

        val imageView =
            view.findViewById<ShapeableImageView>(R.id.favorite)
        var imageSwapped = false

        val rotationAnimator = set.childAnimations.find {
            it is ObjectAnimator && it.propertyName == "alpha"
        } as? ObjectAnimator

        rotationAnimator?.addUpdateListener { anim ->
            if (anim.animatedFraction >= 0.5f && !imageSwapped) {
                val nextIcon = if (isFavorite) {
                    com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_outlined// Going from Favorite to Not
                } else {
                    com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_filled// Going from Not to Favorite
                }
                imageView.setImageResource(nextIcon)
                imageSwapped = true
            }
        }

        set.apply {
            setTarget(view)
            start()
        }
        viewModel.onEvent(TrendingSeriesUiEvent.UpdateTrendingSeriesFavorite(id, !isFavorite))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_screen_movie_trending_series_catalog_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_imdb -> {
//                exitTransition = eTransition
//                reenterTransition = reTransition

                navController.navigate(
                    TrendingSeriesFragmentDirections.movieScreenMovieTrendingSeriesCatalogActionMovieScreenMovieTrendingSeriesCatalogTrendingseriesfragmentToMovieScreenMovieTrendingSeriesSearchNavGraph()
                )
                true
            }

            R.id.fav_imdb -> {
                navController.navigate(
                    TrendingSeriesFragmentDirections.movieScreenMovieTrendingSeriesCatalogActionMovieScreenMovieTrendingSeriesCatalogTrendingseriesfragmentToMovieScreenMovieTrendingSeriesFavoriteNavGraph()
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}