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
import com.rick.data_movie.Movie
import com.rick.screen_movie.databinding.FragmentMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
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

        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = MoviesLoadStateAdapter { adapter.retry() }
        )

        binding.recyclerView.itemAnimator = DefaultItemAnimator()

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

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

        bindList(
            adapter = adapter,
            pagingData = pagingData,
            uiState = uiState,
        )

    }

    private fun FragmentMovieCatalogBinding.bindList(
        adapter: MovieCatalogAdapter,
        pagingData: Flow<PagingData<UiModel>>,
        uiState: StateFlow<UiState>,
    ) {

        lifecycleScope.launchWhenCreated {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collect { loadState ->

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

//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy != 0) onScrollChanged(UiAction.NavigateToDetails(movie))
//            }
//        })

        val hasNavigatedAway = uiState
            .map { it.navigatedAway }
            .distinctUntilChanged()

        val notLoading = adapter.loadStateFlow
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        val shouldScrollToTop = combine(
            hasNavigatedAway,
            notLoading,
            Boolean::and
        )

        lifecycleScope.launch {
            shouldScrollToTop.collectLatest { should ->
                if (should) recyclerView.scrollToPosition(0)
            }
        }

        swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }

    }

    private fun onMovieClick(movie: Movie) {
        UiAction.NavigateToDetails(movie = movie)
        val action = MovieCatalogFragmentDirections
            .actionMovieCatalogFragmentToMovieDetailsFragment(movie)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
//        UiAction.NavigateToDetails(movie = null)
        _binding = null
    }
}