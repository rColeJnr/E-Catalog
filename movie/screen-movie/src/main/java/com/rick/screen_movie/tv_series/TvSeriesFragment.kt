package com.rick.screen_movie.tv_series

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.imdb_am_not_paying.series_model.TvSeries
import com.rick.data_movie.tmdb.trending_tv.TrendingTv
import com.rick.screen_movie.R
import com.rick.screen_movie.UiAction
import com.rick.screen_movie.databinding.FragmentMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TvSeriesFragment : Fragment() {

    private var _binding: FragmentMovieCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TvSeriesViewModel by viewModels()
    private lateinit var adapter: TvSeriesAdapter
    private lateinit var navController: NavController

    private lateinit var eTransition: MaterialSharedAxis
    private lateinit var reTransition: MaterialSharedAxis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieCatalogBinding.inflate(inflater, container, false)

        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        view?.findViewById<Toolbar>(R.id.toolbar)
            ?.setupWithNavController(navController, appBarConfiguration)

        initAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        eTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        reTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }

        binding.bindState(
            pagingData = viewModel.pagingDataFlow
        )
    }
    private fun FragmentMovieCatalogBinding.bindState(
        pagingData: Flow<PagingData<TrendingTv>>
    ) {
        bindList(
            adapter = adapter,
            pagingData = pagingData
        )
    }

    private fun FragmentMovieCatalogBinding.bindList(
        adapter: TvSeriesAdapter,
        pagingData: Flow<PagingData<TvSeriesUiState>>
    ) {

        lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect {

            }
        }

//        tvSeriesLoading.observe(viewLifecycleOwner) {
//            swipeRefresh.isRefreshing = it.loading
//        }
//
//        tvSeriesError.observe(viewLifecycleOwner) {
//            emptyList.isVisible = adapter.itemCount == 0
//            it.msg?.let { msg ->
//                Toast.makeText(
//                    context,
//                    "\uD83D\uDE28 Wooops $msg",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//
//        swipeRefresh.setOnRefreshListener {
//            Toast.makeText(context, getString(R.string.list_updated), Toast.LENGTH_SHORT).show()
//            swipeRefresh.isRefreshing = false
//        }


    }

    private fun onSeriesClick(view: View, series: TvSeries) {
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        val seriesToDetails = getString(R.string.movie_transition_name, series.title)
        val extras = FragmentNavigatorExtras(view to seriesToDetails)
        val action = TvSeriesFragmentDirections
            .actionTvSeriesFragmentToSeriesDetailsFragment(
                series = series.id,
                movieId = null,
                movieTitle = null
            )
        findNavController().navigate(directions = action, navigatorExtras = extras)
    }

    private fun onFavClick(view: View, favorite: Favorite) {
        viewModel.onEvent(UiAction.InsertFavorite(favorite))
    }

    private fun initAdapter() {
        adapter = TvSeriesAdapter(this::onSeriesClick, this::onFavClick)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = DefaultItemAnimator()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
    }

    //    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//        menu.findItem(R.id.search_options).isVisible = false
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_imdb -> {
                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    TvSeriesFragmentDirections.actionTvSeriesFragmentToSeriesSearchFragment()
                )
                true
            }
            R.id.fav_imdb -> {
                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    TvSeriesFragmentDirections.actionTvSeriesFragmentToSeriesFavoriteFragment()
                )

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}