package com.rick.moviecatalog.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rick.moviecatalog.databinding.FragmentMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieCatalogFragment: Fragment() {

    private var _binding: FragmentMovieCatalogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieCatalogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}