package com.rick.screen_movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rick.screen_movie.databinding.FragmentMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

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
        val mActivity = requireActivity()
        adapter = MovieCatalogAdapter(mActivity, this::onMovieClick)

        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter

        binding.bindState(
            uiAction = viewModel.accept,
            uiState = { viewModel.state }
        )

        return binding.root
    }

    private fun FragmentMovieCatalogBinding.bindState(
        uiAction: (UiAction) -> Unit,
        uiState: (UiState) -> Unit
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) Unit
            }
        })

    }

    private fun FragmentMovieCatalogBinding.bindRefresh() {

    }

    private fun onMovieClick(position: Int) {
//        val action = MovieCatalogFragmentDirections.actionMovieCatalogFragmentToMovieDetailsFragment(adapter.moviesDiffer.currentList[position])
//        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}