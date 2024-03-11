package com.rick.screen_book.bestseller_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.accompanist.themeadapter.material.MdcTheme
import com.google.android.material.snackbar.Snackbar
import com.rick.data_book.nytimes.model.NYBook
import com.rick.screen_book.BookLoadStateAdapter
import com.rick.screen_book.ErrorMessage
import com.rick.screen_book.R
import com.rick.screen_book.RemotePresentationState
import com.rick.screen_book.asRemotePresentationState
import com.rick.screen_book.databinding.FragmentBestsellerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BestsellerFragment : Fragment() {

    private var _binding: FragmentBestsellerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BestsellerViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var adapter: BestsellerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBestsellerBinding.inflate(inflater, container, false)

        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        view?.findViewById<Toolbar>(R.id.toolbar)
            ?.setupWithNavController(navController, appBarConfiguration)

        initAdapter()

        binding.bindSpinner()

        return binding.root
    }

    private fun FragmentBestsellerBinding.bindList(
        adapter: BestsellerAdapter,
        pagingDataFlow: Flow<PagingData<NYBook>>,
    ) {

        lifecycleScope.launch {
            pagingDataFlow.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                swipeRefresh.isRefreshing = loadState.mediator?.refresh is LoadState.Loading
                // show empty list.
                composeView.setContent {
                    MdcTheme {
                        ErrorMessage(getString(R.string.no_results))
                    }
                }
                composeView.isVisible =
                    !swipeRefresh.isRefreshing && adapter.itemCount == 0

                val errorState = loadState.source.refresh as? LoadState.Error
                    ?: loadState.mediator?.refresh as? LoadState.Error

                errorState?.let {
                    Toast.makeText(
                        context,
                        "\uD83D\uDE28 Wooops $it",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        val notLoading = adapter.loadStateFlow.asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        lifecycleScope.launch {
            notLoading.collectLatest {
                if (it) recyclerView.scrollToPosition(0)
            }
        }

        swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun FragmentBestsellerBinding.bindSpinner() {
        ArrayAdapter.createFromResource(
            this@BestsellerFragment.requireContext(),
            R.array.bestseller_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                viewModel.onEvent(BestsellerEvents.SelectedGenre(pos))
                bindList(adapter, viewModel.pagingDataFlow)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Do nothing
            }
        }
    }

    private fun initAdapter() {
        adapter = BestsellerAdapter(this::onBookClick, this::onFavoriteClick)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter =
            adapter.withLoadStateFooter(
                footer = BookLoadStateAdapter { adapter.retry() }
            )
    }

    private fun onFavoriteClick(view: View, book: NYBook) {
        //TODO add animation to indicate favorited
        viewModel.onEvent(BestsellerEvents.OnFavoriteClick(book))
        Snackbar.make(view, "Book added to favorites", Snackbar.LENGTH_LONG)
            .setAction("Cancel") {
                viewModel.onEvent(BestsellerEvents.OnRemoveFavorite)
            }
            .show()

    }

    private fun onBookClick(view: View, book: NYBook) {
        // Add transition to expand dialog
        BookDetailsDialogFragment(book, this::onDialogFavoriteClick, this::onAmazonLinkClick).show(
            requireActivity().supportFragmentManager,
            "book_details"
        )
    }

    private fun onDialogFavoriteClick(view: View, book: NYBook) {
        onFavoriteClick(view, book)
    }

    private fun onAmazonLinkClick(link: String) {
        val action = BestsellerFragmentDirections
            .actionBestsellerFragmentToBookDetailsFragment(link)
        navController.navigate(directions = action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}