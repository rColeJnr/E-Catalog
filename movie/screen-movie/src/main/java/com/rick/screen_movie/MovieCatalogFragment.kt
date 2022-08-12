package com.rick.screen_movie

import android.os.Bundle
import android.view.LayoutInflater
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
import androidx.recyclerview.widget.DividerItemDecoration
import com.rick.data_movie.Movie
import com.rick.screen_movie.databinding.FragmentMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieCatalogFragment : Fragment() {

    private var _binding: FragmentMovieCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieCatalogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieCatalogBinding.inflate(inflater, container, false)

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerView.itemAnimator = DefaultItemAnimator()

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
        val adapter =
            MovieCatalogAdapter(requireActivity())

        val header = MoviesLoadStateAdapter { adapter.retry() }
        recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header,
            footer = MoviesLoadStateAdapter { adapter.retry() }
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

        val notLoading = adapter.loadStateFlow
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                //TODO not working at all
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && adapter.itemCount > 0 }
                    ?: loadState.prepend

                val isListEmpty = loadState.refresh is LoadState.NotLoading
                // show empty list.
                emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                        || loadState.mediator?.refresh is LoadState.NotLoading
                // show progress bar during initial load or refresh.
                swipeRefresh.isRefreshing = loadState.mediator?.refresh is LoadState.Loading

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireActivity(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
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