package com.rick.ecatalogs.movie.screen_movie.trending_movie_catalog

import com.rick.ecatalogs.movie.screen_movie.common.R as CommonR
import java.time.format.FormatStyle
import java.time.format.DateTimeParseException
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import kotlinx.coroutines.flow.combine
import com.rick.ecatalogs.movie.screen_movie.common.BrowseRow
import com.rick.ecatalogs.movie.screen_movie.common.BrowseItem
import com.rick.ecatalogs.movie.screen_movie.common.BrowseHeaderState
import com.rick.ecatalogs.movie.screen_movie.common.BrowseHeaderAdapter
import com.rick.ecatalogs.movie.screen_movie.common.BrowseGenre
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.ConcatAdapter
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Build
import android.content.pm.PackageManager
import android.Manifest
import android.animation.AnimatorInflater
import com.rick.ecatalogs.data.ui_design.navigateWithSharedElement
import com.google.android.material.transition.MaterialElevationScale
import androidx.core.view.doOnPreDraw
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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.imageview.ShapeableImageView
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.movie.screen_movie.common.RemotePresentationState
import com.rick.ecatalogs.movie.screen_movie.common.TranslationEvent
import com.rick.ecatalogs.movie.screen_movie.common.TranslationViewModel
import com.rick.ecatalogs.movie.screen_movie.common.asRemotePresentationState
import com.rick.ecatalogs.movie.screen_movie.common.logScreenView
import com.rick.ecatalogs.movie.screen_movie.common.logTrendingMovieOpened
import com.rick.ecatalogs.movie.screen_movie.trending_movie_catalog.databinding.MovieScreenMovieTrendingMovieCatalogFragmentTrendingMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TrendingMovieFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingMovieCatalogFragmentTrendingMovieCatalogBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: TrendingMovieViewModel by viewModels()
    private val translationViewModel: TranslationViewModel by viewModels()

    private lateinit var adapter: TrendingMovieAdapter
    private lateinit var headerAdapter: BrowseHeaderAdapter
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
        _binding = MovieScreenMovieTrendingMovieCatalogFragmentTrendingMovieCatalogBinding.inflate(
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
            viewModel.uiState.asLiveData(),
            adapter = adapter
        )

        analyticsHelper.logScreenView("trendingMovieCatalog")

        // Wait for the list to lay out so the return transition finds the poster
        postponeEnterTransition()
        binding.root.doOnPreDraw { startPostponedEnterTransition() }
        return binding.root
    }

    private fun initAdapter() {
        adapter =
            TrendingMovieAdapter(
                this::onMovieClick,
                this::onFavClick,
                this::onTranslationClick
            )
        binding.recyclerView.itemAnimator = DefaultItemAnimator().apply {
            // Rebinding the same item shouldn't fade the whole card (looked like flicker)
            supportsChangeAnimations = false
        }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        headerAdapter = BrowseHeaderAdapter(
            title = getString(R.string.movie_screen_movie_trending_movie_catalog_header),
            slogan = getString(R.string.movie_screen_movie_trending_movie_catalog_slogan),
            allGenresLabel = getString(CommonR.string.movie_screen_movie_common_browse_all),
            onItemClick = { item -> openMovie(item.id) },
            onBellClick = ::onBellClick,
            onGenreSelected = { genreId ->
                viewModel.selectGenre(genreId)
                binding.recyclerView.scrollToPosition(0)
            },
        )
        binding.recyclerView.adapter = ConcatAdapter(headerAdapter, adapter)
        bindHeader()
    }

    /** Coming soon (with reminders), In theaters, and the genre chips. */
    private fun bindHeader() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.upcoming,
                    viewModel.nowPlaying,
                    viewModel.genres,
                    viewModel.selectedGenre,
                ) { upcoming, nowPlaying, genres, selected ->
                    BrowseHeaderState(
                        rows = listOf(
                            BrowseRow(
                                getString(CommonR.string.movie_screen_movie_common_coming_soon),
                                upcoming.map { (movie, reminded) ->
                                    BrowseItem(
                                        id = movie.id,
                                        title = movie.title,
                                        posterPath = movie.image.ifBlank { null },
                                        subtitle = movie.releaseDate.asReleaseLabel(),
                                        reminderSet = reminded,
                                    )
                                },
                            ),
                            BrowseRow(
                                getString(CommonR.string.movie_screen_movie_common_in_theaters),
                                nowPlaying.map { movie ->
                                    BrowseItem(
                                        id = movie.id,
                                        title = movie.title,
                                        posterPath = movie.image.ifBlank { null },
                                        subtitle = movie.voteAverage.takeIf { it > 0 }?.let {
                                            getString(CommonR.string.movie_screen_movie_common_rating, it)
                                        },
                                    )
                                },
                            ),
                        ),
                        genres = genres.map { BrowseGenre(it.id, it.name) },
                        selectedGenreId = selected,
                    )
                }.collect { headerAdapter.state = it }
            }
        }
    }

    /** "Opens Nov 21" in the user's format. */
    private fun String.asReleaseLabel(): String? = try {
        val date = LocalDate.parse(this).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        getString(CommonR.string.movie_screen_movie_common_opens, date)
    } catch (e: DateTimeParseException) {
        null
    }

    private var pendingReminder: BrowseItem? = null

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // Set the reminder either way; without the permission it just can't be shown
        pendingReminder?.let(::toggleReminder)
        pendingReminder = null
    }

    private fun onBellClick(item: BrowseItem) {
        val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            item.reminderSet == false &&
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            pendingReminder = item
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            toggleReminder(item)
        }
    }

    private fun toggleReminder(item: BrowseItem) {
        viewModel.toggleReminder(item.id)
        val message = if (item.reminderSet == true) {
            getString(CommonR.string.movie_screen_movie_common_reminder_removed)
        } else {
            val releaseDate = viewModel.upcoming.value
                .firstOrNull { it.first.id == item.id }?.first?.releaseDate
            val date = releaseDate?.let {
                runCatching {
                    LocalDate.parse(it).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                }.getOrNull()
            }.orEmpty()
            getString(CommonR.string.movie_screen_movie_common_reminder_set, date)
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun openMovie(id: Int) {
        findNavController().navigateWithMotion(
            Uri.parse("com.rick.ecs://trending_movie_details_fragment/$id")
        )
    }

    private fun MovieScreenMovieTrendingMovieCatalogFragmentTrendingMovieCatalogBinding.bindList(
        uiState: LiveData<TrendingMovieUiState>,
        adapter: TrendingMovieAdapter
    ) {

        uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TrendingMovieUiState.Loading -> {
                    loadingIndicator.isVisible = true
                }

                is TrendingMovieUiState.Success -> {
                    lifecycleScope.launch {
                        state.movies.collect(adapter::submitData)
                    }
                }

                is TrendingMovieUiState.Error -> {
                    loadingIndicator.isVisible = false
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collect { loadState ->
                    // Genre lists come straight from the network (no mediator)
                    loadingIndicator.isVisible =
                        (loadState.mediator?.refresh ?: loadState.source.refresh) is LoadState.Loading
                    // Only once the stored list has been read too, so a cached screen doesn't flash
                // "No results" before its items appear
                emptyList.isVisible = !loadingIndicator.isVisible &&
                    loadState.source.refresh is LoadState.NotLoading &&
                    adapter.itemCount == 0

                    val errorState = loadState.source.refresh as? LoadState.Error
                        ?: loadState.mediator?.refresh as? LoadState.Error

                    errorState?.let {
                        Toast.makeText(
                            requireContext(),
                            getString(
                                R.string.movie_screen_movie_trending_movie_catalog_whoops,
                                it.error.localizedMessage
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

        // No pull-to-refresh (content changes weekly): tap the empty / error message to retry
        emptyList.setOnClickListener {
            adapter.refresh()
        }
    }

    private fun onMovieClick(poster: View, id: Int) {
        val motionDuration = resources
            .getInteger(com.rick.ecatalogs.data.ui_design.R.integer.data_ui_design_motion_duration_long)
            .toLong()
        // The list fades back a little while the poster morphs into the details screen
        exitTransition = MaterialElevationScale(false).apply { duration = motionDuration }
        reenterTransition = MaterialElevationScale(true).apply { duration = motionDuration }
        analyticsHelper.logTrendingMovieOpened(id.toString())
        val uri = Uri.parse("com.rick.ecs://trending_movie_details_fragment/$id")
        findNavController().navigateWithSharedElement(uri, poster)
    }

    private fun onFavClick(view: View, id: Int, isFavorite: Boolean) {
        val set = AnimatorInflater.loadAnimator(
            requireContext(),
            com.rick.ecatalogs.movie.screen_movie.common.R.animator.movie_screen_movie_common_animator
        ) as AnimatorSet


        val imageView = view.findViewById<ShapeableImageView>(R.id.favorite)
        var imageSwapped = false
        val rotationAnimator = set.childAnimations.find {
            it is ObjectAnimator && it.propertyName == "alpha"
        } as? ObjectAnimator

        rotationAnimator?.addUpdateListener { anim ->
            if (anim.animatedFraction >= 0.5f && !imageSwapped) {
                val nextIcon = if (isFavorite) {
                    com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_outlined
                } else {
                    com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_favorite_filled
                }

                imageView.setImageResource(nextIcon)
                imageSwapped = true
            }
        }

        set.apply {
            setTarget(view)
            start()
        }
        viewModel.onEvent(TrendingMovieUiEvent.UpdateTrendingMovieFavorite(id, !isFavorite))
    }

    private fun onTranslationClick(actionView: TextView, textView: TextView, texts: List<String>) {
        if (actionView.text == getString(R.string.movie_screen_movie_trending_movie_catalog_show_original)) {
            textView.animate().alpha(0f).setDuration(200).withEndAction {
                textView.text = texts.first()
                actionView.animate().alpha(0f).setDuration(200).withEndAction {
                    actionView.text =
                        getString(R.string.movie_screen_movie_trending_movie_catalog_show_translation)
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
                                    getString(R.string.movie_screen_movie_trending_movie_catalog_show_original)
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
        inflater.inflate(R.menu.movie_screen_movie_trending_movie_catalog_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_imdb -> {
//                exitTransition = eTransition
//                reenterTransition = reTransition

                navController.navigate(
                    TrendingMovieFragmentDirections.movieScreenMovieTrendingMovieCatalogActionMovieScreenMovieTrendingMovieCatalogTrendingmoviefragmentToMovieScreenMovieTrendingMovieSearchNavGraph()
                )
                true
            }

            R.id.fav_imdb -> {
                navController.navigate(
                    TrendingMovieFragmentDirections.movieScreenMovieTrendingMovieCatalogActionMovieScreenMovieTrendingMovieCatalogTrendingmoviefragmentToMovieScreenMovieTrendingMovieFavoriteNavGraph()
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