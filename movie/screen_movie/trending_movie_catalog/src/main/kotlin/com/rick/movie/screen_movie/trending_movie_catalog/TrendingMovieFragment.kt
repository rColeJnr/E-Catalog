package com.rick.movie.screen_movie.trending_movie_catalog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_movie.UserTrendingMovie
import com.rick.movie.screen_movie.common.RemotePresentationState
import com.rick.movie.screen_movie.common.TranslationEvent
import com.rick.movie.screen_movie.common.TranslationViewModel
import com.rick.movie.screen_movie.common.asRemotePresentationState
import com.rick.movie.screen_movie.common.logScreenView
import com.rick.movie.screen_movie.common.logTrendingMovieOpened
import com.rick.movie.screen_movie.trending_movie_catalog.databinding.MovieScreenMovieTrendingMovieCatalogFragmentTrendingMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TrendingMovieFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingMovieCatalogFragmentTrendingMovieCatalogBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: TrendingMovieViewModel by viewModels()
    private val translationViewModel: TranslationViewModel by viewModels()

    private lateinit var adapter: TrendingMovieAdapter
    private lateinit var navController: NavController

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MovieScreenMovieTrendingMovieCatalogFragmentTrendingMovieCatalogBinding.inflate(
            inflater,
            container,
            false
        )

        navController = findNavController()

        initAdapter()

        if (translationViewModel.location.value.isEmpty()) {
            translationViewModel.setLocation(Locale.getDefault().language)
        }

        binding.bindList(
            viewModel.pagingDataFlow,
            adapter = adapter
        )

        analyticsHelper.logScreenView("trendingMovieCatalog")

        return binding.root
    }

    private fun initAdapter() {
        adapter =
            TrendingMovieAdapter(this::onMovieClick, this::onFavClick, this::onTranslationClick)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
    }

    private fun MovieScreenMovieTrendingMovieCatalogFragmentTrendingMovieCatalogBinding.bindList(
        pagingDataFlow: Flow<PagingData<UserTrendingMovie>>,
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
                        getString(R.string.movie_screen_movie_trending_movie_catalog_whoops, it),
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

    private fun onMovieClick(id: Int) {
        //TODO add animations
        analyticsHelper.logTrendingMovieOpened(id.toString())
        val uri = Uri.parse("com.rick.ecs://trending_movie_details_fragment/$id")
        findNavController().navigate(uri)
    }

    private fun onFavClick(view: View, id: Int, isFavorite: Boolean) {
        viewModel.onEvent(TrendingMovieUiEvent.UpdateTrendingMovieFavorite(id, !isFavorite))
    }

    private fun onTranslationClick(text: View, translation: List<String>) {
        translationViewModel.onEvent(
            TranslationEvent.GetTranslation(
                translation,
                translationViewModel.location.value
            )
        )
        lifecycleScope.launch {
            translationViewModel.translation.collectLatest {
                (text as TextView).text = it.first().text
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_screen_movie_trending_movie_catalog_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_imdb -> {
//                exitTransition = eTransition
//                reenterTransition = reTransition

                navController.navigate(
                    TrendingMovieFragmentDirections.movieScreenMovieTrendingMovieCatalogActionMovieScreenMovieTrendingMovieCatalogTrendingmoviefragmentToMovieScreenMovieTrendingMovieSearchNavGraph()
                )
                true
            }

            R.id.fav_imdb -> {
                navController.navigate(
                    TrendingMovieFragmentDirections.movieScreenMovieTrendingMovieCatalogActionMovieScreenMovieTrendingMovieCatalogTrendingmoviefragmentToMovieScreenMovieTrendingMovieFavoriteNavGraph()
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