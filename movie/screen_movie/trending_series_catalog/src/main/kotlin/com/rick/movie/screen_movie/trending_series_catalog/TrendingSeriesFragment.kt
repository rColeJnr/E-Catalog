package com.rick.movie.screen_movie.trending_series_catalog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.data.model_movie.UserTrendingSeries
import com.rick.movie.screen_movie.trending_series_catalog.databinding.MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrendingSeriesFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: TrendingSeriesViewModel by viewModels()
    private lateinit var adapter: TrendingSeriesAdapter
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
        _binding =
            MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding.inflate(
                inflater,
                container,
                false
            )

        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

//        view?.findViewById<Toolbar>(R.id.toolbar)?.apply {
//            setupWithNavController(navController, appBarConfiguration)


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

    private fun MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding.bindState(
        pagingData: Flow<PagingData<UserTrendingSeries>>
    ) {
        bindList(
            adapter = adapter,
            pagingData = pagingData
        )
    }

    private fun MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding.bindList(
        adapter: TrendingSeriesAdapter,
        pagingData: Flow<PagingData<UserTrendingSeries>>
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

    private fun onSeriesClick(view: View, id: Int) {
        // TODO redo animations
        val uri = Uri.parse("com.rick.ecs/trending_series_details_fragment/$id")
        findNavController().navigate(uri)
    }

    private fun onFavClick(view: View, id: Int, isFavorite: Boolean) {
        viewModel.onEvent(TrendingSeriesUiEvent.UpdateTrendingSeriesFavorite(id, !isFavorite))
    }

    private fun initAdapter() {
        adapter = TrendingSeriesAdapter(this::onSeriesClick, this::onFavClick)

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
                    TrendingSeriesFragmentDirections.movieScreenMovieTrendingSeriesCatalogActionMovieScreenMovieTrendingSeriesCatalogTrendingseriesfragmentToMovieScreenMovieTrendingSeriesSearchNavGraph()
                )
                true
            }

            R.id.fav_imdb -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    TrendingSeriesFragmentDirections.movieScreenMovieTrendingSeriesCatalogActionMovieScreenMovieTrendingSeriesCatalogTrendingseriesfragmentToMovieScreenMovieTrendingSeriesFavoriteNavGraph()
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