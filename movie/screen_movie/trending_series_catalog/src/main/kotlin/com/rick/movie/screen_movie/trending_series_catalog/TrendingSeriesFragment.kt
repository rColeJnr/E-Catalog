package com.rick.movie.screen_movie.trending_series_catalog

import android.R.attr.text
import android.animation.AnimatorInflater
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
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_movie.UserTrendingSeries
import com.rick.movie.screen_movie.common.RemotePresentationState
import com.rick.movie.screen_movie.common.TranslationEvent
import com.rick.movie.screen_movie.common.TranslationViewModel
import com.rick.movie.screen_movie.common.asRemotePresentationState
import com.rick.movie.screen_movie.common.logScreenView
import com.rick.movie.screen_movie.common.logTrendingSeriesOpened
import com.rick.movie.screen_movie.trending_series_catalog.databinding.MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class TrendingSeriesFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: TrendingSeriesViewModel by viewModels()
    private val translationViewModel: TranslationViewModel by viewModels()

    private lateinit var adapter: TrendingSeriesAdapter
    private lateinit var navController: NavController

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding.inflate(
                inflater,
                container,
                false
            )

        navController = findNavController()

        initAdapter()

        if (translationViewModel.location.value.isEmpty()) {
            translationViewModel.setLocation(Locale.getDefault().language)
        }

        binding.bindList(
            viewModel.pagingDataFlow,
            adapter = adapter
        )

        analyticsHelper.logScreenView("trendingMovieCatalog")

        return binding.root
    }

    private fun initAdapter() {
        adapter =
            TrendingSeriesAdapter(
                this::onSeriesClick,
                this::onFavClick,
                this::onTranslationClick
            )
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
    }

    private fun MovieScreenMovieTrendingSeriesCatalogFragmentTrendingSeriesCatalogBinding.bindList(
        pagingDataFlow: Flow<PagingData<UserTrendingSeries>>,
        adapter: TrendingSeriesAdapter
    ) {
        lifecycleScope.launch {
            pagingDataFlow.collectLatest(adapter::submitData)
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
                    android.widget.Toast.makeText(
                        context,
                        "😨 Whoops, ${it.error.message}",
                        android.widget.Toast.LENGTH_SHORT
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

    private fun onSeriesClick(id: Int) {
        //TODO add animations
        analyticsHelper.logTrendingSeriesOpened(id.toString())
        val uri = Uri.parse("com.rick.ecs://trending_series_details_fragment/$id")
        findNavController().navigate(uri)
    }

    private fun onFavClick(view: View, id: Int, isFavorite: Boolean) {
        val set = AnimatorInflater.loadAnimator(
            requireContext(),
            com.rick.movie.screen_movie.common.R.animator.movie_screen_movie_common_animator
        ) as AnimatorSet

        val imageView =
            view.findViewById<ShapeableImageView>(R.id.favorite)
        var imageSwapped = false

        val rotationAnimator = set.childAnimations.find {
            it is ObjectAnimator && it.propertyName == "alpha"
        } as? ObjectAnimator

        rotationAnimator?.addUpdateListener { anim ->
            if (anim.animatedFraction >= 0.5f && !imageSwapped) {
                val nextIcon = if (isFavorite) {
                    com.rick.data.ui_design.R.drawable.data_ui_design_favorite_outlined// Going from Favorite to Not
                } else {
                    com.rick.data.ui_design.R.drawable.data_ui_design_favorite_filled// Going from Not to Favorite
                }
                imageView.setImageResource(nextIcon)
                imageSwapped = true
            }
        }

        set.apply {
            setTarget(view)
            start()
        }
        viewModel.onEvent(TrendingSeriesUiEvent.UpdateTrendingSeriesFavorite(id, !isFavorite))
    }

    private fun onTranslationClick(
        actionView: TextView,
        textView: TextView,
        texts: List<String>
    ) {
        if (actionView.text ==
            getString(R.string.movie_screen_movie_trending_series_catalog_show_original)
        ) {
            textView.animate().alpha(0f).setDuration(200).withEndAction {
                textView.text = texts.first()
                actionView.animate().alpha(0f).setDuration(200).withEndAction {
                    actionView.text = getString(
                        R.string.movie_screen_movie_trending_series_catalog_show_translation
                    )
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
                            actionView.text = getString(
                                R.string.movie_screen_movie_trending_series_catalog_show_original
                            )
                            textView.animate().alpha(1f).setDuration(200).start()
                        }.start()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_screen_movie_trending_series_catalog_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_imdb -> {
//                exitTransition = eTransition
//                reenterTransition = reTransition

                navController.navigate(
                    TrendingSeriesFragmentDirections.movieScreenMovieTrendingSeriesCatalogActionMovieScreenMovieTrendingSeriesCatalogTrendingseriesfragmentToMovieScreenMovieTrendingSeriesSearchNavGraph()
                )
                true
            }

            R.id.fav_imdb -> {
                navController.navigate(
                    TrendingSeriesFragmentDirections.movieScreenMovieTrendingSeriesCatalogActionMovieScreenMovieTrendingSeriesCatalogTrendingseriesfragmentToMovieScreenMovieTrendingSeriesFavoriteNavGraph()
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