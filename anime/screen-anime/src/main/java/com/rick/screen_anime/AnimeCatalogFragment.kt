package com.rick.screen_anime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rick.screen_anime.databinding.FragmentAnimeCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimeCatalogFragment: Fragment() {

    private var _binding: FragmentAnimeCatalogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnimeCatalogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}