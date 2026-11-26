package com.rick.ecatalogs.movie.screen_movie.article_catalog

import com.rick.ecatalogs.data.translation.TextTranslator
import com.rick.ecatalogs.data.ui_design.animateItemsOnScroll
import android.net.Uri
import androidx.recyclerview.widget.SimpleItemAnimator
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
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
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.movie.data_movie.model.UserArticle
import com.rick.ecatalogs.movie.screen_movie.article_catalog.databinding.MovieScreenMovieArticleCatalogFragmentArticleCatalogBinding
import com.rick.ecatalogs.movie.screen_movie.common.ArticleDetailsDialogFragment
import com.rick.ecatalogs.movie.screen_movie.common.RemotePresentationState
import com.rick.ecatalogs.movie.screen_movie.common.asRemotePresentationState
import com.rick.ecatalogs.movie.screen_movie.common.logArticleOpened
import com.rick.ecatalogs.movie.screen_movie.common.logScreenView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@AndroidEntryPoint
class ArticleFragment : Fragment() {

    private var _binding: MovieScreenMovieArticleCatalogFragmentArticleCatalogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NYMovieViewModel by viewModels()
    private lateinit var adapter: ArticleAdapter
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
                resources.getInteger(R.integer.movie_screen_movie_article_catalog_motion_duration_long)
                    .toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MovieScreenMovieArticleCatalogFragmentArticleCatalogBinding.inflate(
            inflater,
            container,
            false
        )

        navController = findNavController()

        initAdapter()

        binding.bindState(
            pagingData = viewModel.pagingDataFLow
        )
        analyticsHelper.logScreenView("articleCatalog")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration =
                resources.getInteger(R.integer.movie_screen_movie_article_catalog_motion_duration_long)
                    .toLong()
        }
        reTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(R.integer.movie_screen_movie_article_catalog_motion_duration_long)
                    .toLong()
        }
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }


    }

    private fun initAdapter() {

        adapter = ArticleAdapter(this::onMovieClick, this::onFavClick, textTranslator)

        // Rebinding the same item shouldn't fade the whole card (looked like flicker)
        (binding.recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        // Cards fade, rise and scale in as they scroll into view
        binding.recyclerView.animateItemsOnScroll()
        binding.recyclerView.adapter =
            adapter

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_screen_movie_article_catalog_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_imdb -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    ArticleFragmentDirections.movieScreenMovieArticleCatalogActionMovieScreenMovieArticleCatalogArticlefragmentToMovieScreenMovieArticleSearchNavGraph()
                )

                true
            }

            R.id.fav_imdb -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    ArticleFragmentDirections.movieScreenMovieArticleCatalogActionMovieScreenMovieArticleCatalogArticlefragmentToMovieScreenMovieArticleFavoriteNavGraph()
                )

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun MovieScreenMovieArticleCatalogFragmentArticleCatalogBinding.bindState(
        pagingData: Flow<PagingData<UserArticle>>
    ) {

        bindList(
            adapter = adapter,
            pagingData = pagingData
        )

    }

    private fun MovieScreenMovieArticleCatalogFragmentArticleCatalogBinding.bindList(
        adapter: ArticleAdapter,
        pagingData: Flow<PagingData<UserArticle>>
    ) {

        viewLifecycleOwner.lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                // show progress bar during initial load or refresh.
                loadingIndicator.isVisible = loadState.mediator?.refresh is LoadState.Loading
                // show empty list.
                // Only once the stored list has been read too, so a cached screen doesn't flash
                // "No results" before its items appear
                emptyList.isVisible = !loadingIndicator.isVisible &&
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
                if (it) recyclerView.scrollToPosition(0)
            }
        }

        // No pull-to-refresh (content changes weekly): tap the empty / error message to retry
        emptyList.setOnClickListener {
            adapter.refresh()
        }

    }

    private fun onMovieClick(article: UserArticle) {
        // Add dialog expand animation
        analyticsHelper.logArticleOpened(article.id.toString())
        ArticleDetailsDialogFragment(
            article,
            this::onDialogFavoriteClick,
            this::onWebUlrClick,
            onFilmClick = { filmId ->
                findNavController().navigateWithMotion(
                    android.net.Uri.parse("com.rick.ecs://trending_movie_details_fragment/$filmId")
                )
            },
        ).show(
            requireActivity().supportFragmentManager,
            "article_details"
        )
    }

    private fun onFavClick(view: View, id: String, isFavorite: Boolean) {
        viewModel.onEvent(NYMovieUiEvent.UpdateArticleFavorite(id, !isFavorite))
    }

    private fun onDialogFavoriteClick(view: View, id: String, isFavorite: Boolean) {
        onFavClick(view, id, isFavorite)
    }

    private fun onWebUlrClick(link: String) {
        val encodedUrl = URLEncoder.encode(link, StandardCharsets.UTF_8.toString())
        val uri = Uri.parse("com.rick.ecs://movie_common_webviewfragment/$encodedUrl")
        findNavController().navigateWithMotion(uri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}