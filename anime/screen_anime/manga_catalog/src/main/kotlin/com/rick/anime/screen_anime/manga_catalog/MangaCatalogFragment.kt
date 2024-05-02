package com.rick.anime.screen_anime.manga_catalog

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
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.anime.anime_screen.common.JikanLoadStateAdapter
import com.rick.anime.anime_screen.common.JikanUiEvents
import com.rick.anime.anime_screen.common.RemotePresentationState
import com.rick.anime.anime_screen.common.asRemotePresentationState
import com.rick.anime.anime_screen.common.logMangaOpened
import com.rick.anime.anime_screen.common.logScreenView
import com.rick.anime.screen_anime.manga_catalog.databinding.AnimeScreenAnimeMangaCatalogFragmentMangaCatalogBinding
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_anime.UserManga
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        enterTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(com.rick.anime.screen_anime.common.R.integer.anime_screen_anime_common_motion_duration_long)
                    .toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AnimeScreenAnimeMangaCatalogFragmentMangaCatalogBinding.inflate(
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
            adapter = adapter,
            pagingDataFlow = viewModel.pagingDataFlow
        )

        analyticsHelper.logScreenView("mangaCatalog")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration =
                resources.getInteger(com.rick.anime.screen_anime.common.R.integer.anime_screen_anime_common_motion_duration_long)
                    .toLong()
        }
        reTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(com.rick.anime.screen_anime.common.R.integer.anime_screen_anime_common_motion_duration_long)
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
        )
//
        binding.jikanRecyclerView.adapter =
            adapter.withLoadStateFooter(footer = JikanLoadStateAdapter { adapter.retry() })

        binding.jikanRecyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun AnimeScreenAnimeMangaCatalogFragmentMangaCatalogBinding.bindList(
        pagingDataFlow: Flow<PagingData<UserManga>>,
        adapter: MangaCatalogAdapter
    ) {

        lifecycleScope.launch {
            pagingDataFlow.collect(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                // show progress bar during initial load or refresh.
                jikanSwipeRefresh.isRefreshing = loadState.mediator?.refresh is LoadState.Loading
                // show empty list.
                jikanEmptyList.isVisible =
                    !jikanSwipeRefresh.isRefreshing && adapter.itemCount == 0

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
                if (it) jikanRecyclerView.scrollToPosition(0)
            }
        }

        jikanSwipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }

    }

    private fun onMangaClick(view: View, id: Int) {
        reenterTransition = MaterialElevationScale(true).apply {
            duration =
                resources.getInteger(com.rick.anime.screen_anime.common.R.integer.anime_screen_anime_common_motion_duration_long)
                    .toLong()
        }
        analyticsHelper.logMangaOpened(id.toString())
        val uri = Uri.parse("com.rick.ecs://manga_details_fragment/$id")
        findNavController().navigate(uri)
    }

    private fun onMangaFavClick(id: Int, isFavorite: Boolean) {
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
                    MangaCatalogFragmentDirections
                        .animeScreenAnimeMangaCatalogActionAnimeScreenAnimeMangaCatalogMangacatalogfragmentToAnimeScreenAnimeMangaSearchNavGraph()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
