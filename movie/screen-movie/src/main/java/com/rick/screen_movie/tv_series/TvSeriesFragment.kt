package com.rick.screen_movie.tv_series

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.rick.data_movie.imdb.series_model.TvSeries
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvSeriesFragment: Fragment() {

    private var _binding: FragmentMovieCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TvSeriesViewModel by viewModels()
    private lateinit var adapter: TvSeriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieCatalogBinding.inflate(inflater, container, false)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        view?.findViewById<Toolbar>(R.id.toolbar)
            ?.setupWithNavController(navController, appBarConfiguration)

        initAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.tvSeriesList.observe(viewLifecycleOwner){
            adapter.differ.submitList(it)
            // TODO show new items thing on top
        }

    }

    private fun onSeriesClick(view: View, series: TvSeries) {
        val action = TvSeriesFragmentDirections
            .actionTvSeriesFragmentToSeriesDetailsFragment(
                series = series.id,
                movieId = null,
                movieTitle = null
            )
        findNavController().navigate(directions = action)
    }

    private fun initAdapter() {
        adapter = TvSeriesAdapter(requireContext(), this::onSeriesClick)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = DefaultItemAnimator()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}