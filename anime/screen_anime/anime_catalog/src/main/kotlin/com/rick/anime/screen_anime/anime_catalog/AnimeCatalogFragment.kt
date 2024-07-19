package com.rick.anime.screen_anime.anime_catalog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.anime.anime_screen.common.JikanLoadStateAdapter
import com.rick.anime.anime_screen.common.JikanUiEvents
import com.rick.anime.anime_screen.common.RemotePresentationState
import com.rick.anime.anime_screen.common.TranslationEvent
import com.rick.anime.anime_screen.common.TranslationViewModel
import com.rick.anime.anime_screen.common.asRemotePresentationState
import com.rick.anime.anime_screen.common.logAnimeOpened
import com.rick.anime.anime_screen.common.logScreenView
import com.rick.anime.screen_anime.anime_catalog.databinding.AnimeScreenAnimeAnimeCatalogFragmentAnimeCatalogBinding
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_anime.UserAnime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AnimeCatalogFragment : Fragment() {

    private var _binding: AnimeScreenAnimeAnimeCatalogFragmentAnimeCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnimeCatalogViewModel by viewModels()
    private val translationViewModel: TranslationViewModel by viewModels()
    private lateinit var adapter: AnimeCatalogAdapter
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
                resources.getInteger(R.integer.anime_screen_anime_anime_catalog_motion_duration_long)
                    .toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AnimeScreenAnimeAnimeCatalogFragmentAnimeCatalogBinding.inflate(
            inflater,
            container,
            false
        )

        navController = findNavController()

        if (translationViewModel.location.value.isEmpty()) {
            translationViewModel.setLocation(Locale.getDefault().language)
        }

        initAdapter()

        binding.bindList(
            adapter = adapter,
            pagingDataFlow = viewModel.pagingDataFlow
        )

        analyticsHelper.logScreenView("AnimeCatalog")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration =
                resources.getInteger(R.integer.anime_screen_anime_anime_catalog_motion_duration_medium)
                    .toLong()
        }
        reTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(R.integer.anime_screen_anime_anime_catalog_motion_duration_medium)
                    .toLong()
        }

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

    }

    private fun initAdapter() {
        adapter = AnimeCatalogAdapter(
            onItemClick = this::onAnimeClick,
            onAnimeFavClick = this::onAnimeFavClick,
            onTranslationClick = this::onTranslationClick
        )

        binding.jikanRecyclerView.adapter =
            adapter.withLoadStateFooter(footer = JikanLoadStateAdapter { adapter.retry() })

        binding.jikanRecyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun AnimeScreenAnimeAnimeCatalogFragmentAnimeCatalogBinding.bindList(
        pagingDataFlow: Flow<PagingData<UserAnime>>,
        adapter: AnimeCatalogAdapter
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
                        getString(R.string.anime_screen_anime_anime_catalog_whoops, it),
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

    private fun onAnimeClick(view: View, id: Int) {
        reenterTransition = MaterialElevationScale(true).apply {
            duration =
                resources.getInteger(R.integer.anime_screen_anime_anime_catalog_motion_duration_long)
                    .toLong()
        }
        analyticsHelper.logAnimeOpened(id.toString())
        val uri =
            Uri.parse("com.rick.ecs://anime_details_fragment/$id")
        findNavController().navigate(uri)
    }

    private fun onAnimeFavClick(id: Int, isFavorite: Boolean) {
        viewModel.onEvent(JikanUiEvents.UpdateAnimeFavorite(id, !isFavorite))
    }

    private fun onTranslationClick(text: View, texts: List<String>) {
        translationViewModel.onEvent(
            TranslationEvent.GetTranslation(
                texts = texts,
                lCode = translationViewModel.location.value
            )
        )
        lifecycleScope.launch {
            translationViewModel.translation.collectLatest {
                (text as TextView).text = it.first().text
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.anime_screen_anime_anime_catalog_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    AnimeCatalogFragmentDirections
                        .animeScreenAnimeAnimeCatalogActionAnimeScreenAnimeAnimeCatalogAnimecatalogfragmentToAnimeScreenAnimeAnimeSearchNavGraph()
                )
                true
            }

            R.id.favorite -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    AnimeCatalogFragmentDirections.animeScreenAnimeAnimeCatalogActionAnimeScreenAnimeAnimeCatalogAnimecatalogfragmentToAnimeScreenAnimeAnimeFavoritesNavGraph()
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