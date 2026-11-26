package com.rick.ecatalogs.game.screen_game.game_catalog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialFadeThrough
import com.rick.ecatalogs.data.analytics.AnalyticsEvent
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.data.ui_components.common.EcsChip
import com.rick.ecatalogs.data.ui_components.common.EcsChipRow
import com.rick.ecatalogs.data.ui_components.common.EcsHeroCarousel
import com.rick.ecatalogs.data.ui_components.common.EcsPosterRow
import com.rick.ecatalogs.data.ui_components.common.EcsSectionTitle
import com.rick.ecatalogs.data.ui_design.animateItemsOnScroll
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import com.rick.ecatalogs.game.screen_game.common.ComposeHeaderAdapter
import com.rick.ecatalogs.game.screen_game.common.GameDeepLinks
import com.rick.ecatalogs.game.screen_game.common.GamePagingAdapter
import com.rick.ecatalogs.game.screen_game.common.asPosterItem
import com.rick.ecatalogs.game.screen_game.game_catalog.databinding.GameScreenGameGameCatalogFragmentGameCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Games catalog: Compose header (coming soon, recently released, genre chips) above the
 * paged "popular now" list. The list stays a RecyclerView (paging-compose isn't a dependency),
 * with Compose rows.
 */
@AndroidEntryPoint
class GameCatalogFragment : Fragment() {

    private var _binding: GameScreenGameGameCatalogFragmentGameCatalogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameCatalogViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    private lateinit var adapter: GamePagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(
                com.rick.ecatalogs.data.ui_design.R.integer.data_ui_design_motion_duration_long
            ).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = GameScreenGameGameCatalogFragmentGameCatalogBinding.inflate(inflater, container, false)
        adapter = GamePagingAdapter(
            onClick = { game -> openGame(game.id) },
            onFavoriteClick = { game, isFavorite -> viewModel.setFavorite(game.id, isFavorite) },
        )
        binding.gameRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            // Rebinding the same item shouldn't fade the whole card
            itemAnimator = DefaultItemAnimator().apply { supportsChangeAnimations = false }
            animateItemsOnScroll()
            adapter = ConcatAdapter(ComposeHeaderAdapter { BrowseHeader() }, this@GameCatalogFragment.adapter)
        }
        bindList()
        analyticsHelper.logEvent(
            AnalyticsEvent(
                type = AnalyticsEvent.Types.SCREEN_VIEW,
                extras = listOf(AnalyticsEvent.Param(AnalyticsEvent.ParamKeys.SCREEN_NAME, "GameCatalog")),
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun bindList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.games.collectLatest(adapter::submitData)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                val refresh = loadState.mediator?.refresh ?: loadState.source.refresh
                binding.gameLoadingIndicator.isVisible = refresh is LoadState.Loading
                // Only once the stored list has been read too, so a cached screen doesn't flash a message
                val failed = refresh is LoadState.Error
                binding.gameEmptyList.isVisible = !binding.gameLoadingIndicator.isVisible &&
                    loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.gameEmptyList.setText(
                    if (failed) com.rick.ecatalogs.game.screen_game.common.R.string.game_screen_game_common_load_failed
                    else com.rick.ecatalogs.game.screen_game.common.R.string.game_screen_game_common_nothing_found
                )
            }
        }
        // Tap the empty / error message to retry
        binding.gameEmptyList.setOnClickListener { adapter.refresh() }
    }

    @Composable
    private fun BrowseHeader() {
        val comingSoon by viewModel.comingSoon.collectAsStateWithLifecycle()
        val recentlyReleased by viewModel.recentlyReleased.collectAsStateWithLifecycle()
        val genres by viewModel.genres.collectAsStateWithLifecycle()
        val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
        Column {
            EcsHeroCarousel(
                title = stringResource(R.string.game_screen_game_game_catalog_coming_soon),
                items = comingSoon.map { it.asPosterItem() },
                onClick = { openGame(it.id) },
            )
            EcsPosterRow(
                title = stringResource(R.string.game_screen_game_game_catalog_recently_released),
                items = recentlyReleased.map { it.asPosterItem() },
                onClick = { openGame(it.id) },
            )
            EcsSectionTitle(
                stringResource(
                    if (selectedGenre == null) R.string.game_screen_game_game_catalog_popular_now
                    else R.string.game_screen_game_game_catalog_most_rated
                )
            )
            val allGenres = EcsChip(ALL_GENRES, stringResource(R.string.game_screen_game_game_catalog_all_genres))
            EcsChipRow(
                chips = listOf(allGenres) + genres.map { EcsChip(it.id.toString(), it.name) },
                selectedId = selectedGenre?.toString() ?: ALL_GENRES,
                onSelect = { chip ->
                    viewModel.selectGenre(chip.id.toIntOrNull())
                    // A new genre starts at the top of its list. Done here, not by collecting
                    // selectedGenre: that StateFlow replays when the view is recreated, which
                    // jumped the list to the top on every return from details.
                    binding.gameRecyclerView.scrollToPosition(0)
                },
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }

    private fun openGame(id: Int) {
        findNavController().navigateWithMotion(GameDeepLinks.details(id))
    }

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.game_screen_game_game_catalog_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
            R.id.game_search -> {
                findNavController().navigateWithMotion(Uri.parse(GameDeepLinks.SEARCH))
                true
            }
            R.id.game_favorites -> {
                findNavController().navigateWithMotion(Uri.parse(GameDeepLinks.FAVORITES))
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private const val ALL_GENRES = "all"
