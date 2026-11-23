package com.rick.ecatalogs.anime.screen_anime.manga_catalog

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
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.anime.screen_anime.common.JikanLoadStateAdapter
import com.rick.ecatalogs.anime.screen_anime.common.JikanUiEvents
import com.rick.ecatalogs.anime.screen_anime.common.RemotePresentationState
import com.rick.ecatalogs.anime.screen_anime.common.TranslationEvent
import com.rick.ecatalogs.anime.screen_anime.common.TranslationViewModel
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
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MangaCatalogFragment : Fragment() {

    private var _binding: AnimeScreenAnimeMangaCatalogFragmentMangaCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaCatalogViewModel by viewModels()
    private val translationViewModel: TranslationViewModel by viewModels()
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

        if (translationViewModel.location.value.isEmpty()) {
            translationViewModel.setLocation(Locale.getDefault().language)
        }

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
            onTranslationClick = this::onTranslationClick
        )
//
        binding.jikanRecyclerView.adapter =
            adapter.withLoadStateFooter(footer = JikanLoadStateAdapter { adapter.retry() })

        binding.jikanRecyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun AnimeScreenAnimeMangaCatalogFragmentMangaCatalogBinding.bindList(
        pagingDataFlow: Flow<PagingData<UserManga>>, adapter: MangaCatalogAdapter
    ) {

        lifecycleScope.launch {
            pagingDataFlow.collect(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                // show progress bar during initial load or refresh.
                jikanSwipeRefresh.isRefreshing = loadState.mediator?.refresh is LoadState.Loading
                // show empty list.
                jikanEmptyList.isVisible = !jikanSwipeRefresh.isRefreshing && adapter.itemCount == 0

                val errorState = loadState.source.refresh as? LoadState.Error
                    ?: loadState.mediator?.refresh as? LoadState.Error

                errorState?.let {
                    Toast.makeText(
                        context,
                        getString(R.string.anime_screen_anime_manga_catalog_whoops, it),
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

    private fun onTranslationClick(actionView: TextView, textView: TextView, texts: List<String>) {
        if (actionView.text == getString(R.string.anime_screen_anime_manga_catalog_show_original)) {
            textView.animate().alpha(0f).setDuration(200).withEndAction {
                textView.text = texts.first()
                actionView.animate().alpha(0f).setDuration(200).withEndAction {
                    actionView.text =
                        getString(R.string.anime_screen_anime_manga_catalog_show_translation)
                    actionView.animate().alpha(1f).setDuration(200).start()
                }.start()
                textView.animate().alpha(1f).setDuration(200).start()
            }.start()
        } else {
            translationViewModel.onEvent(
                TranslationEvent.GetTranslation(
                    texts = texts,
                    lCode = translationViewModel.location.value
                )
            )
            lifecycleScope.launch {
                translationViewModel.translation.collectLatest { translations ->
                    if (translations.isNotEmpty()) {
                        textView.animate().alpha(0f).setDuration(200).withEndAction {
                            textView.text = translations.first().text
                            actionView.animate().alpha(0f).setDuration(200).withEndAction {
                                actionView.text =
                                    getString(R.string.anime_screen_anime_manga_catalog_show_original)
                                actionView.animate().alpha(1f).setDuration(200).start()
                            }.start()
                            textView.animate().alpha(1f).setDuration(200).start()
                        }.start()
                    }
                }
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
