package com.rick.screen_anime.favorite_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rick.screen_anime.databinding.FragmentJikanFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JikanFavoriteFragment : Fragment() {

    private var _binding: FragmentJikanFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: JikanFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJikanFavoriteBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
//            MdcTheme {
            FavScreen(
                anime = viewModel.anime.observeAsState().value ?: emptyList(),
                manga = viewModel.manga.observeAsState().value ?: emptyList(),
                onFavClick = { viewModel.onEvent(JikanEvents.ShouldInsertFavorite(it)) },
                showAnime = viewModel.showAnime.observeAsState().value!!,
                shouldShowAnime = { viewModel.onEvent(JikanEvents.ShouldShowAnime) },
                showManga = viewModel.showManga.observeAsState().value!!,
                shouldShowManga = { viewModel.onEvent(JikanEvents.ShouldShowManga) }
            )
//            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}