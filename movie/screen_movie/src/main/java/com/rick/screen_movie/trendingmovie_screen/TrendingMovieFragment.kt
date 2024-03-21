package com.rick.screen_movie.trendingmovie_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import com.rick.data_movie.tmdb.trending_movie.TrendingMovie
import com.rick.screen_movie.R
import com.rick.screen_movie.RemotePresentationState
import com.rick.screen_movie.asRemotePresentationState
import com.rick.screen_movie.databinding.FragmentMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrendingMovieFragment: Fragment() {

    private var _binding: FragmentMovieCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrendingMovieViewModel by viewModels()

    private lateinit var adapter: TrendingMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieCatalogBinding.inflate(inflater, container, false)

        initAdapter()

        binding.bindList(
            viewModel.pagingDataFlow,
            adapter = adapter
        )

        return binding.root
    }

    private fun initAdapter() {
        adapter = TrendingMovieAdapter(this::onMovieClick, this::onFavClick)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
    }

    private fun FragmentMovieCatalogBinding.bindList(
        pagingDataFlow: Flow<PagingData<TrendingMovie>>,
        adapter: TrendingMovieAdapter
    ) {
        lifecycleScope.launch {
            pagingDataFlow.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                // show progress bar during initial load or refresh.
                swipeRefresh.isRefreshing = loadState.mediator?.refresh is LoadState.Loading
                // show empty list.
                emptyList.isVisible =
                    !swipeRefresh.isRefreshing && adapter.itemCount == 0

                val errorState = loadState.source.refresh as? LoadState.Error
                    ?: loadState.mediator?.refresh as? LoadState.Error

                errorState?.let {
                    Toast.makeText(
                        context,
                        "\uD83D\uDE28 Wooops $it",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val notLoading = adapter.loadStateFlow.asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        lifecycleScope.launch {
            notLoading.collectLatest {
                if (it) recyclerView.scrollToPosition(0)
            }
        }

        swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun onMovieClick(view: View, movie: TrendingMovie){
        //TODO add animations
        val action = TrendingMovieFragmentDirections
            .actionTrendingMovieFragmentToMovieDetailsFragment(movie.id)
        findNavController().navigate(action)
    }

    private fun onFavClick(view: View, movie: TrendingMovie){
        //TODO
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_imdb -> {
//                exitTransition = eTransition
//                reenterTransition = reTransition

                findNavController().navigate(
                    TrendingMovieFragmentDirections.actionTrendingMovieFragmentToSearchFragment()
                )
                true
            }
//            R.id.fav_imdb -> {
//                exitTransition = eTransition
//                reenterTransition = reTransition
//
//                navController.navigate(
//                    TvSeriesFragmentDirections.actionTvSeriesFragmentToSeriesFavoriteFragment()
//                )
//
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}