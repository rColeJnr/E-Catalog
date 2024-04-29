package com.rick.anime.anime_screen.common

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialContainerTransform
import com.rick.anime.screen_anime.common.R
import com.rick.anime.screen_anime.common.databinding.AnimeScreenAnimeCommonWebViewFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewFragment : Fragment() {
    private var _binding: AnimeScreenAnimeCommonWebViewFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var link: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            duration =
                resources.getInteger(R.integer.anime_screen_anime_common_motion_duration_long)
                    .toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AnimeScreenAnimeCommonWebViewFragmentBinding.inflate(inflater, container, false)

        arguments?.let {
            val navArgs = WebViewFragmentArgs.fromBundle(it)

            this.link = navArgs.link
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.webView.transitionName = getString(R.string.book_transition_name, formats?.image)
//        binding.root.transitionName = getString(R.string.search_transition_name, formats?.image)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

        binding.webView.loadUrl(link)
        binding.failedToLoad.visibility =
            if (binding.webView.url.isNullOrBlank()) View.VISIBLE
            else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}