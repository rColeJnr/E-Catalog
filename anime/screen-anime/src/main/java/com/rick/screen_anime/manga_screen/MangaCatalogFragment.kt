package com.rick.screen_anime.manga_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rick.screen_anime.databinding.FragmentMangaCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MangaCatalogFragment: Fragment() {

    private var _binding: FragmentMangaCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaCatalogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMangaCatalogBinding.inflate(inflater, container, false)

        initAdapter()

        return binding.root
    }

    private fun initAdapter() {
        adapter = MangaCatalogAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}