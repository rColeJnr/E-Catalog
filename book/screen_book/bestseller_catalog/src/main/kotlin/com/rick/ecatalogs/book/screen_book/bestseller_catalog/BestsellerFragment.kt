package com.rick.ecatalogs.book.screen_book.bestseller_catalog

import androidx.paging.LoadState
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.core.view.isVisible
import android.animation.AnimatorInflater
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.imageview.ShapeableImageView
import com.rick.ecatalogs.book.screen_book.bestseller_catalog.databinding.BookScreenBookBestsellerCatalogFragmentBestsellerBinding
import com.rick.ecatalogs.book.screen_book.common.TranslationEvent
import com.rick.ecatalogs.book.screen_book.common.TranslationViewModel
import com.rick.ecatalogs.book.screen_book.common.logAmazonLinkOpened
import com.rick.ecatalogs.book.screen_book.common.logBestsellerOpened
import com.rick.ecatalogs.book.screen_book.common.logScreenView
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.book.data_book.model.bestseller.UserBestseller
import com.rick.ecatalogs.data.ui_components.common.ErrorMessage
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
    private lateinit var carouselAdapter: BestsellerCarouselAdapter
    private lateinit var categoryAdapter: BestsellerCategoryAdapter

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

        initAdapters()

        binding.bindList()

        analyticsHelper.logScreenView("bestsellerCatalog")

        return binding.root
    }

    private fun initAdapters() {
        // Main RecyclerView Adapter
        adapter =
            BestsellerAdapter(this::onBookClick, this::onFavoriteClick, this::onTranslationClick)
        binding.recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.recyclerView.itemAnimator = DefaultItemAnimator().apply {
            // Rebinding the same item shouldn't fade the whole card (looked like flicker)
            supportsChangeAnimations = false
        }
        binding.recyclerView.adapter = adapter

        carouselAdapter = BestsellerCarouselAdapter(this::onBookClick, this::onFavoriteClick)
        binding.carouselView.adapter = carouselAdapter

        categoryAdapter = BestsellerCategoryAdapter { position ->
            // The ViewModel switches both the paged list and the carousel to this genre
            viewModel.onEvent(BestsellerEvents.SelectedGenre(position))
            binding.recyclerView.scrollToPosition(0)
        }
        binding.categoryList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryList.adapter = categoryAdapter
        categoryAdapter.setSelectedPosition(viewModel.selectedGenrePosition)
        binding.categoryList.scrollToPosition(viewModel.selectedGenrePosition)
    }

    /**
     * Binds once; switching genre only changes the ViewModel's selected genre.
     * - the paged list (NYT pages lists 20 books at a time) feeds the grid;
     * - the stored list feeds the "Top picks" carousel and reveals the content;
     * - if loading fails with nothing stored, the error card offers a retry.
     */
    private fun BookScreenBookBestsellerCatalogFragmentBestsellerBinding.bindList() {
        categoryAdapter.submitList(BookGenre.entries)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.bestsellerPagingData.collectLatest(adapter::submitData) }

                launch {
                    viewModel.bestsellerUiState.collect { state ->
                        when (state) {
                            BestsellerUIState.Loading -> progressBar.isVisible = true
                            is BestsellerUIState.Success -> {
                                progressBar.isVisible = false
                                bookComposeView.isVisible = false
                                revealContent()
                                carouselAdapter.submitList(
                                    state.bestsellers.sortedByDescending { it.weeksOnList }.take(5)
                                )
                            }
                            BestsellerUIState.Error -> Unit
                        }
                    }
                }

                launch {
                    adapter.loadStateFlow.collect { loadState ->
                        val failed = loadState.mediator?.refresh is LoadState.Error &&
                            adapter.itemCount == 0
                        if (failed) {
                            progressBar.isVisible = false
                            bookComposeView.isVisible = true
                            bookComposeView.setContent {
                                EcsTheme {
                                    ErrorMessage(
                                        getString(R.string.book_screen_book_bestseller_catalog_no_results),
                                    ) { adapter.refresh() }
                                }
                            }
                        } else {
                            bookComposeView.isVisible = false
                        }
                    }
                }
            }
        }
    }

    /** Fades the carousel, categories and grid in the first time there's something to show. */
    private fun BookScreenBookBestsellerCatalogFragmentBestsellerBinding.revealContent() {
        listOf(carouselTitle, carouselView, categoryList, bestsellersLabel, recyclerView)
            .forEachIndexed { index, view ->
                if (view.visibility != View.VISIBLE) {
                    view.apply {
                        alpha = 0f
                        visibility = View.VISIBLE
                        translationY = 50f
                        animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(1000L)
                            .setStartDelay(index * 100L)
                            .setInterpolator(android.view.animation.DecelerateInterpolator())
                            .start()
                    }
                }
            }
    }

    private fun onFavoriteClick(view: View, id: String, isFavorite: Boolean) {
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
        viewModel.onEvent(BestsellerEvents.UpdateBestsellerFavorite(id, !isFavorite))
    }

    private fun onBookClick(book: UserBestseller) {
        analyticsHelper.logBestsellerOpened(book.id)
        findNavController().navigateWithMotion(Uri.parse("com.rick.ecs://bestseller_details/${book.id}"))
    }

    private fun onTranslationClick(book: UserBestseller, text: List<String>) {
        if (this.translationViewModel.location.value.lowercase() == "en") {
            Toast.makeText(
                context,
                getString(R.string.book_screen_book_bestseller_catalog_translation_is_not_available_in_english),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.book_screen_book_bestseller_catalog_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorite -> {
                navController.navigate(
                    BestsellerFragmentDirections.bookScreenBookBestsellerCatalogActionBookScreenBookBestsellerCatalogBestsellerfragmentToBookScreenBookBestsellerFavoritesNavGraph()
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
