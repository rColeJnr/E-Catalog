package com.rick.ecatalogs.anime.screen_anime.manga_catalog

import com.rick.ecatalogs.data.translation.TextTranslator
import com.rick.ecatalogs.data.ui_design.animateItemsOnScroll
import com.rick.ecatalogs.anime.screen_anime.common.JikanHeaderAction
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.rick.ecatalogs.anime.screen_anime.common.R as CommonR
import com.rick.ecatalogs.data.ui_components.common.EcsPosterItem
import com.rick.ecatalogs.data.ui_components.common.EcsChip
import com.rick.ecatalogs.anime.data_anime.model.browse.MangaList
import com.rick.ecatalogs.anime.screen_anime.common.JikanBrowseRow
import com.rick.ecatalogs.anime.screen_anime.common.JikanBrowseHeader
import com.rick.ecatalogs.anime.screen_anime.common.ComposeHeaderAdapter
import com.rick.ecatalogs.anime.screen_anime.common.ALL_GENRES
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.recyclerview.widget.ConcatAdapter
import android.animation.AnimatorInflater
import com.rick.ecatalogs.data.ui_design.navigateWithSharedElement
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
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.anime.screen_anime.common.JikanLoadStateAdapter
import com.rick.ecatalogs.anime.screen_anime.common.JikanUiEvents
import com.rick.ecatalogs.anime.screen_anime.common.RemotePresentationState
import com.rick.ecatalogs.anime.screen_anime.common.asRemotePresentationState
import com.rick.ecatalogs.anime.screen_anime.common.logMangaOpened
import com.rick.ecatalogs.anime.screen_anime.common.logScreenView
import com.rick.ecatalogs.anime.screen_anime.manga_catalog.databinding.AnimeScreenAnimeMangaCatalogFragmentMangaCatalogBinding
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.anime.data_anime.model.UserManga
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MangaCatalogFragment : Fragment() {

    private var _binding: AnimeScreenAnimeMangaCatalogFragmentMangaCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaCatalogViewModel by viewModels()
    private lateinit var adapter: MangaCatalogAdapter
    private lateinit var navController: NavController

    private lateinit var eTransition: MaterialSharedAxis
    private lateinit var reTransition: MaterialSharedAxis

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var textTranslator: TextTranslator

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
    ): View? {
        _binding = AnimeScreenAnimeMangaCatalogFragmentMangaCatalogBinding.inflate(
            inflater, container, false
        )

        navController = findNavController()

        initAdapter()

        binding.bindList(
            adapter = adapter, pagingDataFlow = viewModel.pagingDataFlow
        )

        analyticsHelper.logScreenView("mangaCatalog")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Wait for the list to lay out so the return transition finds the poster
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

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

    //
    private fun initAdapter() {
        adapter = MangaCatalogAdapter(
            onItemClick = this::onMangaClick,
            onMangaFavClick = this::onMangaFavClick,
            translator = textTranslator,
        )
//
        // Cards fade, rise and scale in as they scroll into view
        binding.jikanRecyclerView.animateItemsOnScroll()
        binding.jikanRecyclerView.adapter = ConcatAdapter(
            ComposeHeaderAdapter { BrowseHeader() },
            adapter.withLoadStateFooter(footer = JikanLoadStateAdapter { adapter.retry() }),
        )

        binding.jikanRecyclerView.itemAnimator = DefaultItemAnimator().apply {
            // Rebinding the same item shouldn't fade the whole card (looked like flicker)
            supportsChangeAnimations = false
        }
    }

    private fun AnimeScreenAnimeMangaCatalogFragmentMangaCatalogBinding.bindList(
        pagingDataFlow: Flow<PagingData<UserManga>>, adapter: MangaCatalogAdapter
    ) {

        viewLifecycleOwner.lifecycleScope.launch {
            pagingDataFlow.collect(adapter::submitData)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                // show progress bar during initial load or refresh.
                // Browse lists come straight from Jikan (no mediator)
                jikanLoadingIndicator.isVisible =
                    (loadState.mediator?.refresh ?: loadState.source.refresh) is LoadState.Loading
                // show empty list.
                // Only once the stored list has been read too, so a cached screen doesn't flash
                // "No results" before its items appear
                jikanEmptyList.isVisible = !jikanLoadingIndicator.isVisible &&
                    loadState.source.refresh is LoadState.NotLoading &&
                    adapter.itemCount == 0

                val errorState = loadState.source.refresh as? LoadState.Error
                    ?: loadState.mediator?.refresh as? LoadState.Error

                errorState?.let {
                    Toast.makeText(
                        context,
                        getString(com.rick.ecatalogs.data.ui_components.common.R.string.data_ui_components_common_load_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val notLoading = adapter.loadStateFlow.asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        viewLifecycleOwner.lifecycleScope.launch {
            notLoading.collectLatest {
                if (it) jikanRecyclerView.scrollToPosition(0)
            }
        }

        // No pull-to-refresh (content changes weekly): tap the empty / error message to retry
        jikanEmptyList.setOnClickListener {
            adapter.refresh()
        }

    }

    private fun onMangaClick(view: View, id: Int) {
        val motionDuration = resources
            .getInteger(com.rick.ecatalogs.data.ui_design.R.integer.data_ui_design_motion_duration_long)
            .toLong()
        // The list fades back a little while the poster morphs into the details screen
        exitTransition = MaterialElevationScale(false).apply { duration = motionDuration }
        reenterTransition = MaterialElevationScale(true).apply { duration = motionDuration }
        analyticsHelper.logMangaOpened(id.toString())
        val uri = Uri.parse("com.rick.ecs://manga_details_fragment/$id")
        findNavController().navigateWithSharedElement(uri, view)
    }

    private fun onMangaFavClick(view: View, id: Int, isFavorite: Boolean) {
        val set = AnimatorInflater.loadAnimator(
            requireContext(),
            com.rick.ecatalogs.anime.screen_anime.common.R.animator.anime_screen_anime_common_favorite_animator
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
        viewModel.onEvent(JikanUiEvents.UpdateMangaFavorite(id, !isFavorite))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.anime_screen_anime_manga_catalog_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    MangaCatalogFragmentDirections.animeScreenAnimeMangaCatalogActionAnimeScreenAnimeMangaCatalogMangacatalogfragmentToAnimeScreenAnimeMangaSearchNavGraph()
                )

                true
            }

            R.id.favorite -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    MangaCatalogFragmentDirections.animeScreenAnimeMangaCatalogActionAnimeScreenAnimeMangaCatalogMangacatalogfragmentToAnimeScreenAnimeMangaFavoritesNavGraph()
                )

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Compose header: browse rows, list chips and genre chips (new UI is written in Compose). */
    @Composable
    private fun BrowseHeader() {
        var pickingRandom by remember { mutableStateOf(false) }
        val selectedList by viewModel.selectedList.collectAsStateWithLifecycle()
        val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
        val genres by viewModel.genres.collectAsStateWithLifecycle()
        val publishingNow by viewModel.publishingNow.collectAsStateWithLifecycle()
        val rows = listOf(
            JikanBrowseRow(
                stringResource(CommonR.string.anime_screen_anime_common_publishing_now),
                publishingNow.map { EcsPosterItem(it.id, it.title, it.imageUrl) },
            ),
        )
        JikanBrowseHeader(
            title = stringResource(R.string.anime_screen_anime_manga_catalog_header),
            slogan = stringResource(R.string.anime_screen_anime_manga_catalog_slogan),
            rows = rows,
            lists = listOf(
                EcsChip(MangaList.POPULAR.name, stringResource(CommonR.string.anime_screen_anime_common_list_popular)),
                EcsChip(MangaList.TOP_RATED.name, stringResource(CommonR.string.anime_screen_anime_common_list_top_rated)),
                EcsChip(MangaList.PUBLISHING.name, stringResource(CommonR.string.anime_screen_anime_common_list_publishing)),
                EcsChip(MangaList.FAVORITES.name, stringResource(CommonR.string.anime_screen_anime_common_list_favorites)),
            ),
            selectedList = selectedList.name,
            genres = listOf(EcsChip(ALL_GENRES, stringResource(CommonR.string.anime_screen_anime_common_all_genres))) +
                genres.map { EcsChip(it.id.toString(), it.name) },
            selectedGenre = selectedGenre?.toString() ?: ALL_GENRES,
            onItemClick = { item -> openManga(item.id) },
            onListSelected = { id ->
                viewModel.selectList(MangaList.valueOf(id))
                binding.jikanRecyclerView.scrollToPosition(0)
            },
            onGenreSelected = { id ->
                viewModel.selectGenre(id.takeUnless { it == ALL_GENRES }?.toInt())
                binding.jikanRecyclerView.scrollToPosition(0)
            },
            actions = listOf(
                JikanHeaderAction(
                    label = stringResource(CommonR.string.anime_screen_anime_common_surprise_me),
                    enabled = !pickingRandom,
                ) {
                    pickingRandom = true
                    viewLifecycleOwner.lifecycleScope.launch {
                        val id = viewModel.randomMangaId()
                        pickingRandom = false
                        if (id != null) {
                            openManga(id)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                CommonR.string.anime_screen_anime_common_random_error,
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                },
            ),
        )
    }

    private fun openManga(id: Int) {
        findNavController().navigateWithMotion(Uri.parse("com.rick.ecs://manga_details_fragment/$id"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
