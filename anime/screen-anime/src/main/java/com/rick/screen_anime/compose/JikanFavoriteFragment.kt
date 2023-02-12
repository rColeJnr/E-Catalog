package com.rick.screen_anime.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.screen_anime.databinding.FragmentJikanFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JikanFavoriteFragment: Fragment() {

    var _binding: FragmentJikanFavoriteBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJikanFavoriteBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            MdcTheme {

            }
        }

        return binding.root
    }

}