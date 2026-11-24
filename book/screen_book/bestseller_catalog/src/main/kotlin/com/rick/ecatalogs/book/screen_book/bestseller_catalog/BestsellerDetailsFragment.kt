package com.rick.ecatalogs.book.screen_book.bestseller_catalog

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.book.data_book.model.bestseller.BookReview
import com.rick.ecatalogs.book.data_book.model.bestseller.BuyLink
import com.rick.ecatalogs.book.data_book.model.bestseller.UserBestseller
import com.rick.ecatalogs.book.screen_book.bestseller_catalog.databinding.BookScreenBookBestsellerCatalogFragmentDetailsBinding
import com.rick.ecatalogs.data.ui_components.common.provideGlide
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import com.rick.ecatalogs.data.ui_design.R as DesignR

/**
 * A bestseller's details: cover and list facts, its description, where to buy it, and the New
 * York Times' review of it (headline, summary and opening, with the full article in the app).
 */
@AndroidEntryPoint
class BestsellerDetailsFragment : Fragment() {

    private var _binding: BookScreenBookBestsellerCatalogFragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BestsellerDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val duration = resources.getInteger(DesignR.integer.data_ui_design_motion_duration_medium)
            .toLong()
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).setDuration(duration)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).setDuration(duration)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BookScreenBookBestsellerCatalogFragmentDetailsBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detailsToolbar.apply {
            setNavigationIcon(DesignR.drawable.data_ui_design_back_icon)
            setNavigationOnClickListener { findNavController().navigateUp() }
            inflateMenu(R.menu.book_screen_book_bestseller_catalog_details_menu)
            setOnMenuItemClickListener {
                viewModel.toggleFavorite()
                true
            }
        }
        binding.detailsReviewMessage.setOnClickListener {
            if (viewModel.reviewState.value is ReviewUiState.Error) viewModel.loadReview()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.bookState.collect(::bindBook) }
                launch { viewModel.reviewState.collect(::bindReview) }
            }
        }
    }

    private fun bindBook(state: BookUiState) = with(binding) {
        detailsUnavailable.isVisible = state is BookUiState.Unavailable
        val book = (state as? BookUiState.Success)?.book
        detailsContent.visibility = if (book != null) View.VISIBLE else View.INVISIBLE
        if (book == null) return@with

        detailsToolbar.title = book.title
        detailsCover.contentDescription =
            getString(R.string.book_screen_book_bestseller_catalog_details_cover, book.title)
        if (book.image.isNotBlank()) provideGlide(detailsCover, book.image)
        detailsRank.text = getString(R.string.book_screen_book_bestseller_catalog_rank, book.rank)
        detailsTitle.text = book.title
        detailsAuthor.text = book.author
        detailsWeeks.text = resources.getQuantityString(
            R.plurals.book_screen_book_bestseller_catalog_details_weeks_on_list,
            book.weeksOnList, book.weeksOnList,
        )
        detailsWeeks.isVisible = book.weeksOnList > 0
        // NYT reports rank_last_week = 0 for books that are new on the list
        detailsLastWeek.text = if (book.rankLastWeek > 0) {
            getString(R.string.book_screen_book_bestseller_catalog_details_last_week, book.rankLastWeek)
        } else {
            getString(R.string.book_screen_book_bestseller_catalog_details_new_this_week)
        }
        detailsPublisher.text = book.publisher
        detailsPublisher.isVisible = book.publisher.isNotBlank()

        detailsDescription.text = book.description
        detailsAboutSection.isVisible = book.description.isNotBlank()

        bindStores(book)
        bindFavorite(book.isFavorite)
    }

    /** One chip per store NYT lists (Amazon, Apple Books, Bookshop.org, …). */
    private fun bindStores(book: UserBestseller) = with(binding) {
        val stores = book.buyLinks.filter { it.name.isNotBlank() && it.url.isNotBlank() }
            .ifEmpty {
                // Older entries may only have the Amazon link
                listOfNotNull(book.amazonLink.takeIf { it.isNotBlank() }?.let { BuyLink("Amazon", it) })
            }
        detailsStores.removeAllViews()
        stores.forEach { store ->
            detailsStores.addView(
                Chip(requireContext()).apply {
                    text = store.name
                    setOnClickListener { openExternally(store.url) }
                }
            )
        }
        detailsStoresSection.isVisible = stores.isNotEmpty()
    }

    private fun bindFavorite(isFavorite: Boolean) {
        binding.detailsToolbar.menu.findItem(R.id.details_favorite)?.apply {
            setIcon(
                if (isFavorite) DesignR.drawable.data_ui_design_favorite_filled
                else DesignR.drawable.data_ui_design_favorite_outlined
            )
            title = getString(
                if (isFavorite) R.string.book_screen_book_bestseller_catalog_details_remove_favorite
                else R.string.book_screen_book_bestseller_catalog_details_add_favorite
            )
        }
    }

    private fun bindReview(state: ReviewUiState) = with(binding) {
        detailsReviewLoading.isVisible = state is ReviewUiState.Loading
        detailsReviewCard.isVisible = state is ReviewUiState.Found
        detailsReviewMessage.isVisible =
            state is ReviewUiState.NotReviewed || state is ReviewUiState.Error
        when (state) {
            is ReviewUiState.Found -> bindReviewContent(state.review)
            ReviewUiState.NotReviewed -> detailsReviewMessage.setText(
                R.string.book_screen_book_bestseller_catalog_details_not_reviewed
            )
            ReviewUiState.Error -> detailsReviewMessage.setText(
                R.string.book_screen_book_bestseller_catalog_details_review_error
            )
            ReviewUiState.Loading -> Unit
        }
    }

    private fun bindReviewContent(review: BookReview) = with(binding) {
        detailsReviewImage.isVisible = review.imageUrl != null
        review.imageUrl?.let { provideGlide(detailsReviewImage, it) }
        detailsReviewHeadline.text = review.headline
        detailsReviewHeadline.isVisible = review.headline.isNotBlank()

        val date = review.publishedDate?.formatted()
        detailsReviewMeta.text = when {
            review.reviewer != null && date != null -> getString(
                R.string.book_screen_book_bestseller_catalog_details_review_meta,
                review.reviewer, date,
            )
            else -> review.reviewer ?: date
        }
        detailsReviewMeta.isVisible = !detailsReviewMeta.text.isNullOrEmpty()

        detailsReviewSummary.text = review.summary
        detailsReviewSummary.isVisible = review.summary != null
        detailsReviewExcerpt.text = review.excerpt
        detailsReviewExcerpt.isVisible = review.excerpt != null

        // The rest of the article, read inside the app
        detailsReviewRead.setOnClickListener {
            val encoded = URLEncoder.encode(review.url, StandardCharsets.UTF_8.toString())
            findNavController().navigateWithMotion(
                Uri.parse("com.rick.ecs://book_common_webviewfragment/$encoded")
            )
        }
    }

    /** Stores open in the browser (or the store's app), where the purchase can be completed. */
    private fun openExternally(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            // No browser available
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun String.formatted(): String = try {
    LocalDate.parse(this).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
} catch (e: DateTimeParseException) {
    this
}
