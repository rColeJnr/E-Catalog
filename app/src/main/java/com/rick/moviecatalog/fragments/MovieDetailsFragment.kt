package com.rick.moviecatalog.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rick.moviecatalog.databinding.FragmentMovieDetailsBinding
import com.rick.moviecatalog.viewmodel.MovieCatalogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment: Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieCatalogViewModel by activityViewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}