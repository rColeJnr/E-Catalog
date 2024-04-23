package com.rick.screen_movie.article_screen

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
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.data.model_movie.UserArticle
import com.rick.screen_movie.MoviesLoadStateAdapter
import com.rick.screen_movie.R
import com.rick.screen_movie.RemotePresentationState
import com.rick.screen_movie.asRemotePresentationState
import com.rick.screen_movie.databinding.FragmentMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

//TODO REFACTOR TO NYMovieFragment
@AndroidEntryPoint
class ArticleFragment : Fragment() {

    private var _binding: FragmentMovieCatalogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NYMovieViewModel by viewModels()
    private lateinit var adapter: ArticleAdapter
    private lateinit var navController: NavController

    private lateinit var eTransition: MaterialSharedAxis
    private lateinit var reTransition: MaterialSharedAxis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieCatalogBinding.inflate(inflater, container, false)

        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        view?.findViewById<Toolbar>(R.id.toolbar)
            ?.setupWithNavController(navController, appBarConfiguration)

        initAdapter()

        binding.bindState(
            pagingData = viewModel.pagingDataFLow
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        reTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
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
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.search_imdb).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_imdb -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    ArticleFragmentDirections.actionMovieCatalogFragmentToSearchFragment()
                )

                true
            }
//            R.id.fav_imdb -> {
//
//                exitTransition = eTransition
//                reenterTransition = reTransition
//
//                navController.navigate(
//                    MovieCatalogFragmentDirections.actionMovieCatalogFragmentToMovieFavoriteFragment()
//                )
//
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun FragmentMovieCatalogBinding.bindState(
        pagingData: Flow<PagingData<UserArticle>>
    ) {

        bindList(
            adapter = adapter,
            pagingData = pagingData
        )

    }

    private fun FragmentMovieCatalogBinding.bindList(
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
        ArticleDetailsDialogFragment(
            article,
            this::onDialogFavoriteClick,
            this::onWebUlrClick
        ).show(
            requireActivity().supportFragmentManager,
            "nymovie_details"
        )
    }

    private fun onFavClick(view: View, id: Long, isFavorite: Boolean) {
        viewModel.onEvent(NYMovieUiEvent.UpdateArticleFavorite(id, !isFavorite))
    }

    private fun onDialogFavoriteClick(view: View, id: Long, isFavorite: Boolean) {
        onFavClick(view, id, isFavorite)
    }

    private fun onWebUlrClick(link: String) {
        //TODO implement navigation
        val action = ArticleFragmentDirections
            .actionMovieCatalogFragmentToWebViewFragment(link)
        navController.navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}