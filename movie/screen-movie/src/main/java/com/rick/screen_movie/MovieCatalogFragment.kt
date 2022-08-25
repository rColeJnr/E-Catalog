package com.rick.screen_movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.rick.data_movie.Movie
import com.rick.screen_movie.databinding.FragmentMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieCatalogFragment : Fragment() {

    private var _binding: FragmentMovieCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieCatalogViewModel by viewModels()
    private lateinit var adapter: MovieCatalogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieCatalogBinding.inflate(inflater, container, false)

        adapter =
            MovieCatalogAdapter(requireActivity(), this::onMovieClick)

        binding.bindState(
            uiAction = viewModel.accept,
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFLow
        )

        return binding.root
    }

    private fun FragmentMovieCatalogBinding.bindState(
        uiAction: (UiAction) -> Unit,
        pagingData: Flow<PagingData<UiModel>>,
        uiState: StateFlow<UiState>
    ) {

        val header = MoviesLoadStateAdapter {
            adapter.retry()
        }

        recyclerView.adapter = adapter.withLoadStateHeader(
            header = header,
        )

        bindList(
            adapter, pagingData, header
        )

    }

    private fun FragmentMovieCatalogBinding.bindList(
        adapter: MovieCatalogAdapter,
        pagingData: Flow<PagingData<UiModel>>,
        header: MoviesLoadStateAdapter
    ) {
        swipeRefresh.setOnRefreshListener { adapter.refresh() }


        lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && adapter.itemCount > 0 }
                    ?: loadState.prepend

                // show empty list.
                emptyList.isVisible =
                    loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                // Only show the list if refresh succeeds.
                recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                // show progress bar during initial load or refresh.
                swipeRefresh.isRefreshing = loadState.mediator?.refresh is LoadState.Loading

            }
        }
    }

    private fun onMovieClick(movie: Movie) {
        val action = MovieCatalogFragmentDirections
            .actionMovieCatalogFragmentToMovieDetailsFragment(movie)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}