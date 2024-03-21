package com.rick.screen_movie.favorite_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.screen_movie.databinding.FragmentMovieFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFavoriteFragment : Fragment() {

    private var _binding: FragmentMovieFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieFavoriteBinding.inflate(inflater, container, false)

        binding.compoeView.setContent {
            MdcTheme {
                FavScreen(
                    articles = viewModel.nyMovie.observeAsState().value ?: emptyList(),
                    movies = viewModel.movie.observeAsState().value ?: emptyList(),
                    series = viewModel.series.observeAsState().value ?: emptyList(),
                    showArticles = viewModel.showArticles.collectAsState().value,
                    showMovies = viewModel.showMovies.collectAsState().value,
                    showSeries = viewModel.showSeries.collectAsState().value,
                    shouldShowArticles = { viewModel.onEvent(UiEvents.ShouldShowArticles) },
                    shouldShowMovies = { viewModel.onEvent(UiEvents.ShouldShowMovies) },
                    shouldShowSeries = { viewModel.onEvent(UiEvents.ShouldShowSeries) },
                    onFavClick = { viewModel.onEvent(UiEvents.ShouldInsertFavorite(it)) }
                )
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}