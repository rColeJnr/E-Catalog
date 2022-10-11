package com.rick.screen_book.search_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_book.model.Formats
import com.rick.screen_book.BookCatalogAdapter
import com.rick.screen_book.BookCatalogViewHolder
import com.rick.screen_book.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment: Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

class SearchAdapter(
    private val onItemClick: (view: View, formats: Formats) -> Unit
): RecyclerView.Adapter<BookCatalogViewHolder>() {

    private val diffUtil = BookCatalogAdapter.DIFF_UTIL

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookCatalogViewHolder {
        return BookCatalogViewHolder.create(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: BookCatalogViewHolder, position: Int) {
        val book = differ.currentList[position]
        holder.bind(book)
    }

    override fun getItemCount(): Int =
        differ.currentList.size
}

