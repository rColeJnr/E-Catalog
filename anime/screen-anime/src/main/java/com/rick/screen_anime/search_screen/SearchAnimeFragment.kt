package com.rick.screen_anime.search_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rick.screen_anime.databinding.FragmentSearchAnimeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchAnimeFragment: Fragment() {

    private var _binding: FragmentSearchAnimeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchAnimeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}