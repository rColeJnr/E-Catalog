package com.rick.screen_movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
        pagingData: Flow<PagingData<Result>>,
        uiState: StateFlow<UiState>
    ) {
        val adapter =
            MovieCatalogAdapter(requireActivity(), this@MovieCatalogFragment::onMovieClick)

        recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header = MoviesLoadStateAdapter { adapter.retry()},
            footer = MoviesLoadStateAdapter { adapter.retry()}
        )

        bindList(
            adapter, pagingData
        )
        bindRefresh(

        )

    }

    private fun FragmentMovieCatalogBinding.bindList(
        adapter: MovieCatalogAdapter,
        pagingData: Flow<PagingData<Result>>
    ) {
        lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }
    }

    private fun FragmentMovieCatalogBinding.bindRefresh() {
        swipeRefresh.setOnRefreshListener {
        }

        swipeRefresh.isRefreshing 
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