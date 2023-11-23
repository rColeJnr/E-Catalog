package com.rick.screen_book.bestseller_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.rick.data_book.nytimes.model.NYBook
import com.rick.screen_book.BookLoadStateAdapter
import com.rick.screen_book.databinding.FragmentBookCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BestsellerFragment: Fragment() {

    private var _binding: FragmentBookCatalogBinding? = null
    private val binding get()= _binding!!
    private val viewModel: BestsellerViewModel by viewModels()
    private lateinit var adapter: BestsellerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookCatalogBinding.inflate(inflater, container, false)

        initAdapter()

        return binding.root
    }

    private fun initAdapter(){
        adapter = BestsellerAdapter(this::onBookClick, this::onFavoriteClick)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter =
            adapter.withLoadStateFooter(
                footer = BookLoadStateAdapter { adapter.retry() }
            )
    }

    private fun onFavoriteClick(book: NYBook){
        //TODO add animation to indicate favorited
        viewModel.onEvent(BestsellerEvents.OnFavoriteClick(book))
    }

    private fun onBookClick(book: NYBook){
        BookDetailsDialogFragment().show(requireActivity().supportFragmentManager, "book_details")
        viewModel.onEvent(BestsellerEvents.OnBookClick(book))
    }

}