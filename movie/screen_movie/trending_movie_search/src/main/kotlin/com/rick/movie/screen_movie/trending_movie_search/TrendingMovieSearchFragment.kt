package com.rick.movie.screen_movie.trending_movie_search

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.model_movie.UserTrendingMovie
import com.rick.data.ui_components.common.RecentSearchesBody
import com.rick.movie.screen_movie.common.logScreenView
import com.rick.movie.screen_movie.common.logTrendingMovieOpened
import com.rick.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.movie.screen_movie.common.util.provideGlide
import com.rick.movie.screen_movie.trending_movie_search.databinding.MovieScreenMovieTrendingMovieSearchFragmentSearchTrendingMovieBinding
import com.rick.movie.screen_movie.trending_movie_search.databinding.MovieScreenMovieTrendingMovieSearchSearchEntryBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrendingMovieSearchFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingMovieSearchFragmentSearchTrendingMovieBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: TrendingMovieSearchViewModel by viewModels()
    private lateinit var searchAdapter: TrendingMovieSearchAdapter

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration =
                resources.getInteger(R.integer.movie_screen_movie_trending_movie_search_motion_duration_long)
                    .toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(R.integer.movie_screen_movie_trending_movie_search_motion_duration_long)
                    .toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MovieScreenMovieTrendingMovieSearchFragmentSearchTrendingMovieBinding.inflate(
            inflater,
            container,
            false
        )

        binding.toolbar.apply {
            inflateMenu(R.menu.movie_screen_movie_trending_movie_search_menu)

//            menu.findItem(R.id.fav_imdb).isVisible = false

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.search -> {
                        binding.updateListFromInput(onQueryChanged = {
                            viewModel.onEvent(
                                TrendingMovieSearchUiEvent.SearchQuery(it)
                            )
                        })
                        true
                    }

                    else -> super.onOptionsItemSelected(item)
                }
            }
            setNavigationIcon(R.drawable.movie_screen_movie_trending_movie_search_ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        initAdapter()

        binding.bindState(
            onQueryChanged = {
                viewModel.onEvent(TrendingMovieSearchUiEvent.SearchQuery(it))
                viewModel.onEvent(TrendingMovieSearchUiEvent.OnSearchTriggered(it))
            },
            uiState = viewModel.searchState.asLiveData(),
            recentSearchesUiState = viewModel.recentSearchState.asLiveData()
        )

        analyticsHelper.logScreenView("trendingMovieSearch")

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
        searchAdapter = TrendingMovieSearchAdapter(
            this::onTrendingMovieClick, this::onTrendingMovieFavClick
        )

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)
    }

    private fun MovieScreenMovieTrendingMovieSearchFragmentSearchTrendingMovieBinding.bindState(
        onQueryChanged: (String) -> Unit,
        uiState: LiveData<TrendingMovieSearchUiState>,
        recentSearchesUiState: LiveData<TrendingMovieRecentSearchQueriesUiState>
    ) {

        list.adapter = searchAdapter

        bindSearch(
            onQueryChanged = onQueryChanged
        )

        bindList(
            adapter = searchAdapter,
            uiState = uiState,
            recentSearchesUiState = recentSearchesUiState,
            onClearRecentSearches = { viewModel.onEvent(TrendingMovieSearchUiEvent.ClearRecentSearches) },
            onRecentSearchClicked = {
                viewModel.onEvent(TrendingMovieSearchUiEvent.SearchQuery(it))
                viewModel.onEvent(TrendingMovieSearchUiEvent.OnSearchTriggered(it))
            }
        )
    }

    private fun MovieScreenMovieTrendingMovieSearchFragmentSearchTrendingMovieBinding.bindSearch(
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

//        lifecycleScope.launch {
//            uiState
//                .map { it.query }
//                .distinctUntilChanged()
//                .collectLatest(searchInput::setText)
//        }
    }

    private fun MovieScreenMovieTrendingMovieSearchFragmentSearchTrendingMovieBinding.updateListFromInput(
        onQueryChanged: (String) -> Unit
    ) {
        searchInput.text!!.trim().let { query ->
            if (query.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(query.toString())
            }
        }
    }

    private fun MovieScreenMovieTrendingMovieSearchFragmentSearchTrendingMovieBinding.bindList(
        adapter: TrendingMovieSearchAdapter,
        uiState: LiveData<TrendingMovieSearchUiState>,
        recentSearchesUiState: LiveData<TrendingMovieRecentSearchQueriesUiState>,
        onClearRecentSearches: () -> Unit,
        onRecentSearchClicked: (String) -> Unit,
    ) {
        uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                TrendingMovieSearchUiState.EmptyQuery -> {
                    recentSearchesUiState.observe(viewLifecycleOwner) { state ->
                        if (state is TrendingMovieRecentSearchQueriesUiState.Success) {
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

                is TrendingMovieSearchUiState.Error -> {
                    Log.e(TAG, "da error: ${state.msg}")
                    if (adapter.searchDiffer.currentList.isEmpty()) {

                        searchErrorMessage.visibility = View.VISIBLE
                    } else {
                        searchErrorMessage.visibility = View.GONE
                    }
                }

                TrendingMovieSearchUiState.Loading -> searchProgressBar.visibility = View.VISIBLE
                is TrendingMovieSearchUiState.Success -> {
                    recentSearchesComposeView.visibility = View.GONE
                    searchProgressBar.visibility = View.GONE
                    adapter.searchDiffer.submitList(state.movies)
                }
            }
        }
    }

    private fun onTrendingMovieClick(view: View, id: Int) {
        analyticsHelper.logTrendingMovieOpened(id.toString())
        val uri = Uri.parse("com.rick.ecs://trending_movie_details_fragment/$id")
        findNavController().navigate(uri)
    }

    private fun onTrendingMovieFavClick(id: Int, isFavorite: Boolean) {
        viewModel.onEvent(TrendingMovieSearchUiEvent.UpdateFavorite(id, !isFavorite))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

private const val TAG = "TrendingMovieSearchFragment"

class TrendingMovieSearchAdapter(
    private val onItemClicked: (View, Int) -> Unit,
    private val onFavClicked: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<TrendingMovieSearchViewHolder>() {

    private val searchDiffUtil = object : DiffUtil.ItemCallback<UserTrendingMovie>() {
        override fun areItemsTheSame(
            oldItem: UserTrendingMovie, newItem: UserTrendingMovie
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UserTrendingMovie, newItem: UserTrendingMovie
        ): Boolean {
            return oldItem == newItem
        }
    }

    val searchDiffer = AsyncListDiffer(this, searchDiffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TrendingMovieSearchViewHolder {
        return TrendingMovieSearchViewHolder.create(parent, onItemClicked, onFavClicked)
    }

    override fun onBindViewHolder(
        holder: TrendingMovieSearchViewHolder, position: Int
    ) {
        val searchResult = searchDiffer.currentList[position]
        (holder).bind(searchResult)
    }

    override fun getItemCount(): Int = searchDiffer.currentList.size
}

class TrendingMovieSearchViewHolder(
    binding: MovieScreenMovieTrendingMovieSearchSearchEntryBinding,
    private val onItemClicked: (view: View, Int) -> Unit,
    private val onFavClicked: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.movieImage
    private val title = binding.movieName
    private val description = binding.movieSummary
    private val rootLayout = binding.movieEntryCardView
    private val favorite = binding.favButton
    private val resources = itemView.resources


    init {
        binding.root.setOnClickListener {
            onItemClicked(it, searchResult.id)
        }
        favorite.setOnClickListener {
            onFavClicked(searchResult.id, searchResult.isFavorite)
        }
    }

    private lateinit var searchResult: UserTrendingMovie

    fun bind(searchResult: UserTrendingMovie) {
        this.rootLayout.transitionName = "search ${searchResult.id}"
        this.searchResult = searchResult
        this.title.text = searchResult.title
        this.description.text = searchResult.overview
        searchResult.image.let { image ->
            provideGlide(
                this.image, getTmdbImageUrl(image)
            )
        }
        favorite.foreground = if (searchResult.isFavorite) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.movie_screen_movie_trending_movie_search_ic_fav_filled,
                null
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.movie_screen_movie_trending_movie_search_ic_fav_outlined,
                null
            )
        }
    }

//    overridee fun onClick(v: View) {
//        onItemClicked(v, searchResult)
//    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClicked: (View, Int) -> Unit,
            onFavClicked: (Int, Boolean) -> Unit
        ): TrendingMovieSearchViewHolder {
            val itemBinding =
                MovieScreenMovieTrendingMovieSearchSearchEntryBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            return TrendingMovieSearchViewHolder(
                itemBinding, onItemClicked, onFavClicked
            )
        }
    }
}