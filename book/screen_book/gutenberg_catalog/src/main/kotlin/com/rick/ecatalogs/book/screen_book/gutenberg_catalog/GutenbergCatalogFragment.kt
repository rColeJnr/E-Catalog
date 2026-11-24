package com.rick.ecatalogs.book.screen_book.gutenberg_catalog

import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ConcatAdapter
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.book.screen_book.common.BookLoadStateAdapter
import com.rick.ecatalogs.book.screen_book.common.RemotePresentationState
import com.rick.ecatalogs.book.screen_book.common.asRemotePresentationState
import com.rick.ecatalogs.book.screen_book.common.logGutenbergOpened
import com.rick.ecatalogs.book.screen_book.common.logScreenView
import com.rick.ecatalogs.book.screen_book.gutenberg_catalog.databinding.BookScreenBookGutenbergCatalogFragmentGutenbergCatalogBinding
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.book.data_book.model.gutenberg.UserGutenberg
import com.rick.ecatalogs.data.ui_components.common.ErrorMessage
import com.rick.ecatalogs.book.screen_book.common.gutenbergReaderUri
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
    private lateinit var headerAdapter: GutenbergHeaderAdapter
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
                resources.getInteger(com.rick.ecatalogs.data.ui_design.R.integer.data_ui_design_motion_duration_long)
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
                resources.getInteger(com.rick.ecatalogs.data.ui_design.R.integer.data_ui_design_motion_duration_long)
                    .toLong()
        }
        reTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(com.rick.ecatalogs.data.ui_design.R.integer.data_ui_design_motion_duration_long)
                    .toLong()
        }
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

    }

    private fun initAdapter() {
        adapter = GutenbergCatalogAdapter(this::onBookClick, this::onGutenbergFavClick)

        headerAdapter = GutenbergHeaderAdapter { filter ->
            viewModel.setFilter(filter)
            binding.recyclerView.scrollToPosition(0)
        }
        binding.recyclerView.adapter = ConcatAdapter(
            headerAdapter,
            adapter.withLoadStateFooter(footer = BookLoadStateAdapter { adapter.retry() }),
        )
        val layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        // The header (filters) and the loading footer span both columns
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                if (position == 0 || position > adapter.itemCount) 2 else 1
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filter.collect { headerAdapter.filter = it }
            }
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.itemAnimator = DefaultItemAnimator().apply {
            // Rebinding the same item shouldn't fade the whole card (looked like flicker)
            supportsChangeAnimations = false
        }
    }

    private fun BookScreenBookGutenbergCatalogFragmentGutenbergCatalogBinding.bindList(
        adapter: GutenbergCatalogAdapter, pagingDataFlow: Flow<PagingData<UserGutenberg>>
    ) {


        lifecycleScope.launch {
            pagingDataFlow.collect(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                // show empty list.
                bookComposeView.setContent {
                    EcsTheme {
                        ErrorMessage(getString(R.string.book_screen_book_gutenberg_catalog_no_results))
                    }
                }
                bookComposeView.isVisible = adapter.itemCount == 0

                val errorState = loadState.source.refresh as? LoadState.Error
                    ?: loadState.mediator?.refresh as? LoadState.Error

                errorState?.let {
                    Toast.makeText(
                        context,
                        getString(R.string.book_screen_book_gutenberg_catalog_wooops, it),
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
    }

    private fun onBookClick(view: View, book: UserGutenberg) {
        // Read in the app (HTML or plain text). Never the RDF metadata or the EPUB zip.
        val link = book.formats.readableUrl ?: return
        analyticsHelper.logGutenbergOpened(link)
        findNavController().navigateWithMotion(gutenbergReaderUri(book.id, book.title, link))
    }

    private fun onGutenbergFavClick(view: View, id: Int, isFavorite: Boolean) {
        val set = AnimatorInflater.loadAnimator(
            requireContext(),
            com.rick.ecatalogs.book.screen_book.common.R.animator.book_screen_book_common_favorite_animator
        ) as AnimatorSet

        val imageView = view.findViewById<ShapeableImageView>(R.id.favorite)
        var imageSwapped = false

        val rotationAnimator = set.childAnimations.find {
            it is ObjectAnimator && it.propertyName == "alpha"
        } as? ObjectAnimator

        rotationAnimator?.addUpdateListener { anim ->
            if (anim.animatedFraction >= 0.5f && !imageSwapped) {
                val nextIcon = if (isFavorite) {
                    com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_outlined // Going from Favorite to Not
                } else {
                    com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_filled // Going from Not to Favorite
                }
                imageView.setImageResource(nextIcon)
                imageSwapped = true
            }
        }

        set.apply {
            setTarget(view)
            start()
        }
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