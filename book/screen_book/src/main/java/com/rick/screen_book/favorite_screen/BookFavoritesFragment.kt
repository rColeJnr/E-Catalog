package com.rick.screen_book.favorite_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rick.screen_book.databinding.FragmentBookFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookFavoritesFragment : Fragment() {

    private var _binding: FragmentBookFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookFavoriteBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            val books = viewModel.books.observeAsState()
            FavScreen(favorites = books.value ?: listOf())
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}