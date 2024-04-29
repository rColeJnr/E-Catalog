package com.rick.anime.screen_anime.anime_search

import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.transition.MaterialElevationScale
import com.rick.anime.screen_anime.anime_search.databinding.AnimeScreenAnimeAnimeSearchFragmentAnimeSearchBinding
import com.rick.data.ui_components.common.RecentSearchesBody
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimeSearchFragment : Fragment() {

    private var _binding: AnimeScreenAnimeAnimeSearchFragmentAnimeSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnimeSearchViewModel by viewModels()
    private lateinit var searchAdapter: AnimeSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
//            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
//        }
//        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
//            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AnimeScreenAnimeAnimeSearchFragmentAnimeSearchBinding.inflate(
            inflater,
            container,
            false
        )

        binding.toolbar.apply {
            inflateMenu(R.menu.anime_screen_anime_anime_search_menu)

//            menu.findItem(R.id.fav_imdb).isVisible = false

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.search -> {
                        binding.updateListFromInput(onQueryChanged = {
                            viewModel.onEvent(
                                AnimeSearchUiEvent.SearchQuery(it)
                            )
                        })
                        true
                    }

                    else -> super.onOptionsItemSelected(item)
                }
            }

            setNavigationIcon(R.drawable.anime_screen_anime_anime_search_ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        initAdapter()

        binding.bindState(
            onQueryChanged = {
                viewModel.onEvent(AnimeSearchUiEvent.SearchQuery(it))
                viewModel.onEvent(AnimeSearchUiEvent.OnSearchTriggered(it))
            },
            uiState = viewModel.searchState.asLiveData(),
            recentSearchesUiState = viewModel.recentSearchState.asLiveData()
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun initAdapter() {
        val circularProgressDrawable = CircularProgressDrawable(requireContext()).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }
        val options = RequestOptions().placeholder(circularProgressDrawable)
        val glide = Glide.with(requireContext())
        searchAdapter = AnimeSearchAdapter(
            this::onAnimeClick, this::onAnimeFavClick
        )

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)
    }

    private fun AnimeScreenAnimeAnimeSearchFragmentAnimeSearchBinding.bindState(
        onQueryChanged: (String) -> Unit,
        uiState: LiveData<AnimeSearchUiState>,
        recentSearchesUiState: LiveData<AnimeRecentSearchQueriesUiState>
    ) {

        list.adapter = searchAdapter

        bindSearch(
            onQueryChanged = onQueryChanged
        )

        bindList(adapter = searchAdapter,
            uiState = uiState,
            recentSearchesUiState = recentSearchesUiState,
            onClearRecentSearches = { viewModel.onEvent(AnimeSearchUiEvent.ClearRecentSearches) },
            onRecentSearchClicked = {
                viewModel.onEvent(AnimeSearchUiEvent.SearchQuery(it))
                viewModel.onEvent(AnimeSearchUiEvent.OnSearchTriggered(it))
            })
    }

    private fun AnimeScreenAnimeAnimeSearchFragmentAnimeSearchBinding.bindSearch(
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

    private fun AnimeScreenAnimeAnimeSearchFragmentAnimeSearchBinding.updateListFromInput(
        onQueryChanged: (String) -> Unit
    ) {
        searchInput.text!!.trim().let { query ->
            if (query.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(query.toString())
            }
        }
    }

    private fun AnimeScreenAnimeAnimeSearchFragmentAnimeSearchBinding.bindList(
        adapter: AnimeSearchAdapter,
        uiState: LiveData<AnimeSearchUiState>,
        recentSearchesUiState: LiveData<AnimeRecentSearchQueriesUiState>,
        onClearRecentSearches: () -> Unit,
        onRecentSearchClicked: (String) -> Unit,
    ) {

        uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                AnimeSearchUiState.EmptyQuery -> {
                    recentSearchesUiState.observe(viewLifecycleOwner) { state ->
                        if (state is AnimeRecentSearchQueriesUiState.Success) {
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

                AnimeSearchUiState.Error -> {
                    if (adapter.differ.currentList.isEmpty()) {

                        searchErrorMessage.visibility = View.VISIBLE
                    } else {
                        searchErrorMessage.visibility = View.GONE
                    }
                }

                AnimeSearchUiState.Loading -> searchProgressBar.visibility = View.VISIBLE
                is AnimeSearchUiState.Success -> {
                    recentSearchesComposeView.visibility = View.GONE
                    searchProgressBar.visibility = View.GONE
                    adapter.differ.submitList(state.animes)
                }
            }
        }
    }

    //TODO (REMOVE .toString())
    private fun onAnimeClick(view: View, id: Int) {
        exitTransition = MaterialElevationScale(false).apply {
            duration =
                resources.getInteger(R.integer.anime_screen_anime_anime_search_motion_duration_long)
                    .toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration =
                resources.getInteger(R.integer.anime_screen_anime_anime_search_motion_duration_long)
                    .toLong()
        }
        val uri = Uri.parse("com.rick.ecs://anime_details_fragment/$id")
        findNavController().navigate(uri)
    }

    private fun onAnimeFavClick(id: Int, isFavorite: Boolean) {
        viewModel.onEvent(AnimeSearchUiEvent.UpdateFavorite(id, !isFavorite))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}