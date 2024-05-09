package com.rick.movie.screen_movie.article_catalog

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
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_movie.UserArticle
import com.rick.movie.screen_movie.article_catalog.databinding.MovieScreenMovieArticleCatalogFragmentArticleCatalogBinding
import com.rick.movie.screen_movie.common.ArticleDetailsDialogFragment
import com.rick.movie.screen_movie.common.MoviesLoadStateAdapter
import com.rick.movie.screen_movie.common.RemotePresentationState
import com.rick.movie.screen_movie.common.asRemotePresentationState
import com.rick.movie.screen_movie.common.logArticleOpened
import com.rick.movie.screen_movie.common.logScreenView
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

        adapter = ArticleAdapter(this::onMovieClick, this::onFavClick)

        binding.recyclerView.adapter =
            adapter.withLoadStateFooter(footer = MoviesLoadStateAdapter { adapter.retry() })

        binding.recyclerView.itemAnimator = DefaultItemAnimator()

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

        lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                // show progress bar during initial load or refresh.
                swipeRefresh.isRefreshing = loadState.mediator?.refresh is LoadState.Loading
                // show empty list.
                emptyList.isVisible =
                    !swipeRefresh.isRefreshing && adapter.itemCount == 0

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
                if (it) recyclerView.scrollToPosition(0)
            }
        }

        swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }

    }

    private fun onMovieClick(view: View, article: UserArticle) {
        // Add dialog expand animation
        analyticsHelper.logArticleOpened(article.id.toString())
        ArticleDetailsDialogFragment(
            article,
            this::onDialogFavoriteClick,
            this::onWebUlrClick
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
        findNavController().navigate(uri)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}