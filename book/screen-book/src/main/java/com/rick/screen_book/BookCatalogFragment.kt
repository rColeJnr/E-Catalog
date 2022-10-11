package com.rick.screen_book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.rick.screen_book.databinding.FragmentBookCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookCatalogFragment: Fragment() {

    private var _binding: FragmentBookCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookCatalogViewModel by viewModels()
    private lateinit var adapter: BookCatalogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookCatalogBinding.inflate(inflater, container, false)

        initAdapter()

        return binding.root
    }

    private fun initAdapter(){
        adapter = BookCatalogAdapter()

        binding.recyclerView.adapter =
            adapter.withLoadStateFooter(footer = BookLoadStateAdapter { adapter.retry() })

        binding.recyclerView.itemAnimator = DefaultItemAnimator()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}