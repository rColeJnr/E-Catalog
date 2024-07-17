package com.rick.book.screen_book.bestseller_catalog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.rick.book.screen_book.bestseller_catalog.databinding.BookScreenBookBestsellerCatalogFragmentBestsellerBinding
import com.rick.book.screen_book.common.TranslationEvent
import com.rick.book.screen_book.common.TranslationViewModel
import com.rick.book.screen_book.common.logAmazonLinkOpened
import com.rick.book.screen_book.common.logBestsellerOpened
import com.rick.book.screen_book.common.logScreenView
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_book.bestseller.UserBestseller
import com.rick.data.ui_components.common.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class BestsellerFragment : Fragment() {

    private var _binding: BookScreenBookBestsellerCatalogFragmentBestsellerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BestsellerViewModel by viewModels()
    private val translationViewModel: TranslationViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var adapter: BestsellerAdapter

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BookScreenBookBestsellerCatalogFragmentBestsellerBinding.inflate(
            inflater, container, false
        )

        navController = findNavController()

        if (translationViewModel.location.value.isEmpty()) {
            translationViewModel.setLocation(Locale.getDefault().language)
        }

        initAdapter()

        binding.bindSpinner()

        analyticsHelper.logScreenView("bestsellerCatalog")

        return binding.root
    }

    private fun BookScreenBookBestsellerCatalogFragmentBestsellerBinding.bindSpinner() {
        ArrayAdapter.createFromResource(
            this@BestsellerFragment.requireContext(),
            R.array.book_screen_book_bestseller_catalog_bestseller_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                viewModel.onEvent(BestsellerEvents.SelectedGenre(pos))
                bindList(
                    adapter,
                    viewModel.bestsellerUiState.asLiveData(),
                    onRetry = { viewModel.onEvent(BestsellerEvents.SelectedGenre(pos)) })
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Do nothing
            }
        }
    }

    private fun BookScreenBookBestsellerCatalogFragmentBestsellerBinding.bindList(
        adapter: BestsellerAdapter,
        uiState: LiveData<BestsellerUIState>,
        onRetry: () -> Unit
    ) {

        uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                BestsellerUIState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    bookComposeView.visibility = View.GONE
                }

                is BestsellerUIState.Success -> {
                    progressBar.visibility = View.GONE
                    bookComposeView.visibility = View.GONE
                    adapter.differ.submitList(state.bestsellers)
                }

                BestsellerUIState.Error -> {
                    progressBar.visibility = View.GONE
                    bookComposeView.visibility = View.VISIBLE
                    bookComposeView.setContent {
                        ErrorMessage(
                            getString(R.string.book_screen_book_bestseller_catalog_no_results),
                            onRetry
                        )
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        adapter =
            BestsellerAdapter(this::onBookClick, this::onFavoriteClick, this::onTranslationClick)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
    }

    private fun onFavoriteClick(view: View, id: String, isFavorite: Boolean) {
        //TODO add animation to indicate favorited
        viewModel.onEvent(BestsellerEvents.UpdateBestsellerFavorite(id, !isFavorite))
    }

    private fun onBookClick(book: UserBestseller) {
        // Add transition to expand dialog
        analyticsHelper.logBestsellerOpened(book.id)
        BookDetailsDialogFragment(book, this::onDialogFavoriteClick, this::onAmazonLinkClick).show(
            requireActivity().supportFragmentManager, "book_details"
        )
    }

    private fun onTranslationClick(book: UserBestseller, text: List<String>) {
        translationViewModel.onEvent(
            TranslationEvent.GetTranslation(
                text,
                translationViewModel.location.value
            )
        )
        lifecycleScope.launch {
            translationViewModel.translation.collectLatest {
                onBookClick(book.copy(description = it.first().text))
            }
        }
    }

    private fun onDialogFavoriteClick(view: View, book: UserBestseller) {
        onFavoriteClick(view, book.id, book.isFavorite)
    }

    private fun onAmazonLinkClick(link: String) {
        val encodedUrl = URLEncoder.encode(link, StandardCharsets.UTF_8.toString())
        analyticsHelper.logAmazonLinkOpened(encodedUrl)
        val uri = Uri.parse("com.rick.ecs://book_common_webviewfragment/$encodedUrl")
        findNavController().navigate(uri)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.book_screen_book_bestseller_catalog_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
//            R.id.search_books -> {
//
//                true
//            }

            R.id.favorite -> {

                navController.navigate(
                    BestsellerFragmentDirections.bookScreenBookBestsellerCatalogActionBookScreenBookBestsellerCatalogBestsellerfragmentToBookScreenBookBestsellerFavoritesNavGraph()
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

private const val TAG = "BestsellerFragment"