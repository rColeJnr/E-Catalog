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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import com.rick.data_movie.Result
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

        recyclerView.adapter = adapter.withLoadStateFooter(
            footer = MoviesLoadStateAdapter { adapter.retry() }
        )

        bindList(
            adapter, pagingData
        )

    }

    private fun FragmentMovieCatalogBinding.bindList(
        adapter: MovieCatalogAdapter,
        pagingData: Flow<PagingData<UiModel>>
    ) {
        swipeRefresh.setOnRefreshListener { adapter.retry() }

        lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                val isListEmpty = adapter.itemCount == 0
                // show empty list.
                emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                recyclerView.isVisible = !isListEmpty
                // show progress bar during initial load or refresh.
                swipeRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading

            }
        }
    }
    private fun onMovieClick(result: Result) {
        val action = MovieCatalogFragmentDirections
            .actionMovieCatalogFragmentToMovieDetailsFragment(result)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}