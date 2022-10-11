package com.rick.screen_book.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rick.data_book.model.Formats
import com.rick.screen_book.databinding.FragmentBookDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailsFragment : Fragment() {
    private var _binding: FragmentBookDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var formats: Formats

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailsBinding.inflate(inflater, container, false)

        arguments?.let {
            val navArgs = BookDetailsFragmentArgs.fromBundle(it)

            formats = navArgs.formats
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formats.textHtml?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textHtmlCharsetIso88591?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textHtmCharsetUtf8?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textPlain?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textPlainCharsetUtf8?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textHtmlCharsetUsAscii?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textPlainCharsetUsAscii?.let {
            binding.webView.loadUrl(it)
        }

        binding.failedToLoad.visibility =
            if (binding.webView.url.isNullOrBlank()) View.VISIBLE
            else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}