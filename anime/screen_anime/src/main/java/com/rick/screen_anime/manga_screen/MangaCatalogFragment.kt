package com.rick.screen_anime.manga_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
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
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.data.model_anime.UserManga
import com.rick.screen_anime.JikanLoadStateAdapter
import com.rick.screen_anime.R
import com.rick.screen_anime.RemotePresentationState
import com.rick.screen_anime.anime_screen.JikanUiEvents
import com.rick.screen_anime.asRemotePresentationState
import com.rick.screen_anime.databinding.AnimeScreenAnimeFragmentJikanCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MangaCatalogFragment : Fragment() {

    private var _binding: AnimeScreenAnimeFragmentJikanCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaCatalogViewModel by viewModels()
    private lateinit var adapter: MangaCatalogAdapter
    private lateinit var navController: NavController

    private lateinit var eTransition: MaterialSharedAxis
    private lateinit var reTransition: MaterialSharedAxis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        enterTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(R.integer.anime_screen_anime_catalog_motion_duration_long)
                    .toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AnimeScreenAnimeFragmentJikanCatalogBinding.inflate(inflater, container, false)

        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        view?.findViewById<Toolbar>(R.id.toolbar)
            ?.setupWithNavController(navController, appBarConfiguration)

        initAdapter()

        binding.bindList(
            adapter = adapter,
            pagingDataFlow = viewModel.pagingDataFlow
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration =
                resources.getInteger(R.integer.anime_screen_anime_catalog_motion_duration_long)
                    .toLong()
        }
        reTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(R.integer.anime_screen_anime_catalog_motion_duration_long)
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

    private fun AnimeScreenAnimeFragmentJikanCatalogBinding.bindList(
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

    private fun onMangaClick(view: View, jikan: UserManga) {
        reenterTransition = MaterialElevationScale(true).apply {
            duration =
                resources.getInteger(R.integer.anime_screen_anime_catalog_motion_duration_long)
                    .toLong()
        }
        val action =
            MangaCatalogFragmentDirections.actionMangaCatalogFragmentToMangaDetailsJikanFragment(

            )
        navController.navigate(action)
    }

    private fun onMangaFavClick(id: Int, isFavorite: Boolean) {
        viewModel.onEvent(JikanUiEvents.UpdateMangaFavorite(id, !isFavorite))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.anime_screen_anime_jikan_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_jikan -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    MangaCatalogFragmentDirections.actionMangaCatalogFragmentToMangaSearchJikanFragment()
                )

                true
            }
//            R.id.fav_jikan -> {
//
//                exitTransition = eTransition
//                reenterTransition = reTransition
//
//                navController.navigate(
//                    MangaCatalogFragmentDirections.actionMangaCatalogFragmentToMangaFavoriteFragment()
//                )
//
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
