package com.rick.moviecatalog.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rick.moviecatalog.MainActivity
import com.rick.moviecatalog.R
import com.rick.moviecatalog.adapters.MovieCatalogAdapter
import com.rick.moviecatalog.databinding.FragmentMovieCatalogBinding
import com.rick.moviecatalog.viewmodel.MovieCatalogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieCatalogFragment : Fragment() {

    private var _binding: FragmentMovieCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieCatalogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieCatalogBinding.inflate(inflater, container, false)
        val mActivity = activity as MainActivity
        val adapter = MovieCatalogAdapter(mActivity, this::onMovieClick)

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

        viewModel.movieList.observe(viewLifecycleOwner) { list ->
            viewModel.movieMutableList.addAll(list)
            // ответ API иногда отправляет один и тот же фильм
            viewModel.movieMutableList.toSet()
            adapter.moviesDiffer.submitList(viewModel.movieMutableList.toList())
        }


        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!viewModel.isLoading.value!!) {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.moviesDiffer.currentList.size - 1) {
                        // проверьте, есть ли еще доступные данные из API
                        if (viewModel.hasMore.value == true) viewModel.loadMoreData()
                    }
                }
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank())
                Toast.makeText(context, getString(R.string.error_toast_message, it), Toast.LENGTH_LONG)
                    .show()
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) {
            binding.root.isRefreshing = it
        }

        viewModel.hasMore.observe(viewLifecycleOwner) {
            if (!it) Toast.makeText(context, getString(R.string.no_more_movies), Toast.LENGTH_SHORT)
                .show()
        }

        binding.root.setOnRefreshListener {
            viewModel.refreshData()
        }
        return binding.root
    }

    private fun onMovieClick(int: Int) {
        findNavController().navigate(R.id.action_movieCatalogFragment_to_movieDetailsFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}