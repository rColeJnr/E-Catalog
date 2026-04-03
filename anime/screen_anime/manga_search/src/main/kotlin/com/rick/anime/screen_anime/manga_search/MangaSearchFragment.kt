package com.rick.anime.screen_anime.manga_search

import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.anime.anime_screen.common.logMangaOpened
import com.rick.anime.anime_screen.common.logScreenView
import com.rick.anime.screen_anime.manga_search.databinding.AnimeScreenAnimeMangaSearchFragmentMangaSearchBinding
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.ui_components.common.RecentSearchesBody
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MangaSearchFragment : Fragment() {

    private var _binding: AnimeScreenAnimeMangaSearchFragmentMangaSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaSearchViewModel by viewModels()
    private lateinit var searchAdapter: MangaSearchAdapter

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration =
                resources.getInteger(com.rick.data.ui_design.R.integer.data_ui_design_motion_duration_long)
                    .toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(com.rick.data.ui_design.R.integer.data_ui_design_motion_duration_long)
                    .toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AnimeScreenAnimeMangaSearchFragmentMangaSearchBinding.inflate(
            inflater, container, false
        )

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.setPadding(0, systemBars.top, 0, 0)
            binding.list.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        binding.toolbar.apply {
            inflateMenu(R.menu.anime_screen_anime_manga_search_menu)

//            menu.findItem(R.id.fav_imdb).isVisible = false

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.search -> {
                        binding.updateListFromInput(onQueryChanged = {
                            viewModel.onEvent(
                                MangaSearchUiEvent.SearchQuery(it)
                            )
                        })
                        true
                    }

                    else -> super.onOptionsItemSelected(item)
                }
            }

            setNavigationIcon(com.rick.data.ui_design.R.drawable.data_ui_design_back_icon)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        initAdapter()

        binding.bindState(
            onQueryChanged = {
                viewModel.onEvent(MangaSearchUiEvent.SearchQuery(it))
                viewModel.onEvent(MangaSearchUiEvent.OnSearchTriggered(it))
            },
            uiState = viewModel.searchState.asLiveData(),
            recentSearchesUiState = viewModel.recentSearchState.asLiveData()
        )
        analyticsHelper.logScreenView("mangaSearch")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun initAdapter() {
        searchAdapter = MangaSearchAdapter(
            this::onMangaClick, this::onMangaFavClick
        )

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)
    }

    private fun AnimeScreenAnimeMangaSearchFragmentMangaSearchBinding.bindState(
        onQueryChanged: (String) -> Unit,
        uiState: LiveData<MangaSearchUiState>,
        recentSearchesUiState: LiveData<MangaRecentSearchQueriesUiState>
    ) {

        list.adapter = searchAdapter

        bindSearch(
            onQueryChanged = onQueryChanged
        )

        bindList(
            adapter = searchAdapter,
            uiState = uiState,
            recentSearchesUiState = recentSearchesUiState,
            onClearRecentSearches = { viewModel.onEvent(MangaSearchUiEvent.ClearRecentSearches) },
            onRecentSearchClicked = {
                viewModel.onEvent(MangaSearchUiEvent.SearchQuery(it))
                viewModel.onEvent(MangaSearchUiEvent.OnSearchTriggered(it))
            })
    }

    private fun AnimeScreenAnimeMangaSearchFragmentMangaSearchBinding.bindSearch(
        onQueryChanged: (String) -> Unit,
    ) {

//        showSoftKeyboard(searchInput, requireContext())

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchInput.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
    }

    private fun AnimeScreenAnimeMangaSearchFragmentMangaSearchBinding.updateListFromInput(
        onQueryChanged: (String) -> Unit
    ) {
        searchInput.text!!.trim().let { query ->
            if (query.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(query.toString())
            }
        }
    }

    private fun AnimeScreenAnimeMangaSearchFragmentMangaSearchBinding.bindList(
        adapter: MangaSearchAdapter,
        uiState: LiveData<MangaSearchUiState>,
        recentSearchesUiState: LiveData<MangaRecentSearchQueriesUiState>,
        onClearRecentSearches: () -> Unit,
        onRecentSearchClicked: (String) -> Unit,
    ) {

        uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                MangaSearchUiState.EmptyQuery -> {
                    recentSearchesUiState.observe(viewLifecycleOwner) { state ->
                        if (state is MangaRecentSearchQueriesUiState.Success) {
                            recentSearchesComposeView.visibility = View.VISIBLE
                            recentSearchesComposeView.setContent {
                                RecentSearchesBody(
                                    onClearRecentSearches = onClearRecentSearches,
                                    onRecentSearchClicked = onRecentSearchClicked,
                                    recentSearchQueries = state.recentQueries.map { it.query },
                                )
                            }
                        }
                    }
                }

                MangaSearchUiState.Error -> {
                    if (adapter.differ.currentList.isEmpty()) {

                        searchErrorMessage.visibility = View.VISIBLE
                    } else {
                        searchErrorMessage.visibility = View.GONE
                    }
                }

                MangaSearchUiState.Loading -> searchProgressBar.visibility = View.VISIBLE
                is MangaSearchUiState.Success -> {
                    recentSearchesComposeView.visibility = View.GONE
                    searchProgressBar.visibility = View.GONE
                    adapter.differ.submitList(state.mangas)
                }
            }
        }
    }

    private fun onMangaClick(view: View, id: Int) {
        exitTransition = MaterialElevationScale(false).apply {
            duration =
                resources.getInteger(com.rick.data.ui_design.R.integer.data_ui_design_motion_duration_long)
                    .toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration =
                resources.getInteger(com.rick.data.ui_design.R.integer.data_ui_design_motion_duration_long)
                    .toLong()
        }
        analyticsHelper.logMangaOpened(id.toString())
        val uri = Uri.parse("com.rick.ecs://manga_details_fragment/$id")
        findNavController().navigate(uri)
    }

    private fun onMangaFavClick(id: Int, isFavorite: Boolean) {
        viewModel.onEvent(MangaSearchUiEvent.UpdateFavorite(id, !isFavorite))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}