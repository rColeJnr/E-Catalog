package com.rick.book.screen_book.gutenberg_catalog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.book.screen_book.common.BookLoadStateAdapter
import com.rick.book.screen_book.common.RemotePresentationState
import com.rick.book.screen_book.common.asRemotePresentationState
import com.rick.book.screen_book.common.logGutenbergOpened
import com.rick.book.screen_book.common.logScreenView
import com.rick.book.screen_book.gutenberg_catalog.databinding.BookScreenBookGutenbergCatalogFragmentGutenbergCatalogBinding
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_book.gutenberg.Formats
import com.rick.data.model_book.gutenberg.UserGutenberg
import com.rick.data.ui_components.common.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject


@AndroidEntryPoint
class GutenbergCatalogFragment : Fragment() {

    private var _binding: BookScreenBookGutenbergCatalogFragmentGutenbergCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GutenbergCatalogViewModel by viewModels()
    private lateinit var adapter: GutenbergCatalogAdapter
    private lateinit var navController: NavController

    private lateinit var eTransition: MaterialSharedAxis
    private lateinit var reTransition: MaterialSharedAxis

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        enterTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(R.integer.book_screen_book_gutenberg_catalog_motion_duration_long)
                    .toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BookScreenBookGutenbergCatalogFragmentGutenbergCatalogBinding.inflate(
            inflater,
            container,
            false
        )

        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

//        view?.findViewById<Toolbar>(R.id.toolbar)
//            ?.setupWithNavController(navController, appBarConfiguration)

        initAdapter()

        binding.bindList(
            adapter = adapter, viewModel.pagingDataFlow
        )
        analyticsHelper.logScreenView("gutenbergCatalog")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration =
                resources.getInteger(R.integer.book_screen_book_gutenberg_catalog_motion_duration_long)
                    .toLong()
        }
        reTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(R.integer.book_screen_book_gutenberg_catalog_motion_duration_long)
                    .toLong()
        }
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

    }

    private fun initAdapter() {
        adapter = GutenbergCatalogAdapter(this::onBookClick, this::onGutenbergFavClick)

        binding.recyclerView.adapter =
            adapter.withLoadStateFooter(footer = BookLoadStateAdapter { adapter.retry() })

        binding.recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun BookScreenBookGutenbergCatalogFragmentGutenbergCatalogBinding.bindList(
        adapter: GutenbergCatalogAdapter, pagingDataFlow: Flow<PagingData<UserGutenberg>>
    ) {


        lifecycleScope.launch {
            pagingDataFlow.collect(adapter::submitData)
        }


        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                // show progress bar during initial load or refresh.
                swipeRefresh.isRefreshing = loadState.mediator?.refresh is LoadState.Loading
                // show empty list.
                bookComposeView.setContent {
                    ErrorMessage(getString(R.string.book_screen_book_gutenberg_catalog_no_results))
                }
                bookComposeView.isVisible = !swipeRefresh.isRefreshing && adapter.itemCount == 0

                val errorState = loadState.source.refresh as? LoadState.Error
                    ?: loadState.mediator?.refresh as? LoadState.Error

                errorState?.let {
                    Toast.makeText(
                        context, "\uD83D\uDE28 Wooops $it", Toast.LENGTH_SHORT
                    ).show()
                }

                swipeRefresh.setOnRefreshListener { swipeRefresh.isRefreshing = false }
            }
        }

        val notLoading = adapter.loadStateFlow.asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        lifecycleScope.launch {
            notLoading.collectLatest {
                if (it) recyclerView.scrollToPosition(0)
            }
        }
    }

    private fun onBookClick(view: View, formats: Formats) {
        val link: String? = formats.run {
            this.textPlain ?: this.textHtml ?: this.textPlainCharsetUtf8
        }

        link?.let {
            analyticsHelper.logGutenbergOpened(link)
            val encodedUrl = URLEncoder.encode(link, StandardCharsets.UTF_8.toString())
            val uri = Uri.parse("com.rick.ecs://movie_common_webviewfragment/$encodedUrl")
            findNavController().navigate(uri)
        }

    }

    private fun onGutenbergFavClick(id: Int, isFavorite: Boolean) {
        //how do i even begin to do this?? lol
        // we need to call view model and add the favorite to  db
        // at some point we'LL need to call the db for favorites
        // and present them in the same ui as the remote data.§§  this sounds like
        // loads of fun.
        // It's was way too much work to do this.  I'm doing something else.
        viewModel.onEvent(BookUiEvents.UpdateGutenbergFavorite(id, !isFavorite))

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.book_screen_book_gutenberg_catalog_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    GutenbergCatalogFragmentDirections.bookScreenBookGutenbergCatalogActionBookScreenBookGutenbergCatalogGutenbergcatalogfragmentToBookScreenBookGutenbergSearchNavGraph()
                )
                true
            }

            R.id.favorite -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    GutenbergCatalogFragmentDirections.bookScreenBookGutenbergCatalogActionBookScreenBookGutenbergCatalogGutenbergcatalogfragmentToBookScreenBookGutenbergFavoritesNavGraph()
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private const val TAG = "BookCatalogFragment"