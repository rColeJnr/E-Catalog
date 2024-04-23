package com.rick.screen_movie.search_screen

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.data.model_movie.tmdb.search.Search
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentSearchBinding
import com.rick.screen_movie.databinding.MovieEntryBinding
import com.rick.screen_movie.util.getTmdbImageUrl
import com.rick.screen_movie.util.provideGlide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.toolbar.apply {
            inflateMenu(R.menu.search_menu)

//            menu.findItem(R.id.fav_imdb).isVisible = false

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.search_imdb -> {
                        binding.updateListFromInput(viewModel.searchAction)
                        true
                    }

                    else -> super.onOptionsItemSelected(item)
                }
            }

            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        initAdapter()

        binding.bindState(
            searchList = viewModel.searchList,
            searchLoading = viewModel.searchLoading,
            searchError = viewModel.searchError,
            uiAction = viewModel.searchAction,
            uiState = viewModel.searchState,
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
        searchAdapter = SearchAdapter(
            this::onMovieClick,
            this::onFavClick
        )

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)
    }

    private fun FragmentSearchBinding.bindState(
        searchList: LiveData<SearchUiState.Response>,
        searchLoading: LiveData<SearchUiState.Loading>,
        searchError: LiveData<SearchUiState.Error>,
        uiAction: (SearchUiEvent) -> Unit,
        uiState: StateFlow<SearchUiState.Query>
    ) {

        list.adapter = searchAdapter

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiAction
        )

        bindList(
            adapter = searchAdapter,
            searchList = searchList,
            searchLoading = searchLoading,
            searchError = searchError,
        )
    }

    private fun FragmentSearchBinding.bindSearch(
        uiState: StateFlow<SearchUiState.Query>,
        onQueryChanged: (SearchUiEvent) -> Unit,
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

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collectLatest(searchInput::setText)
        }
    }

    private fun FragmentSearchBinding.updateListFromInput(onQueryChanged: (SearchUiEvent.SearchQuery) -> Unit) {
        searchInput.text!!.trim().let { query ->
            if (query.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(SearchUiEvent.SearchQuery(query = query.toString()))

            }
        }
    }

    private fun FragmentSearchBinding.bindList(
        adapter: SearchAdapter,
        searchList: LiveData<SearchUiState.Response>,
        searchLoading: LiveData<SearchUiState.Loading>,
        searchError: LiveData<SearchUiState.Error>
    ) {
        lifecycleScope.launch {
            searchList.observe(viewLifecycleOwner) {
                adapter.searchDiffer.submitList(it.response)
            }
        }

        lifecycleScope.launch {
            searchLoading.observe(viewLifecycleOwner) {
                if (it.loading) searchProgressBar.visibility = View.VISIBLE
                else searchProgressBar.visibility = View.GONE
            }

            searchError.observe(viewLifecycleOwner) {
                if (adapter.searchDiffer.currentList.isEmpty() && it.msg != null ) {
                    searchErrorMessage.visibility = View.VISIBLE
                } else {
                    searchErrorMessage.visibility = View.GONE
                }
            }
        }
    }

    //TODO (REMOVE .toString())
    private fun onMovieClick(view: View, movie: com.rick.data.model_movie.tmdb.search.Search) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        val searchToDetails = getString(R.string.search_transition_name, movie.id.toString())
        val extras = FragmentNavigatorExtras(view to searchToDetails)
        val action =
            SearchFragmentDirections
                .actionSeriesSearchFragmentToTvDetailsFragment(
                    movie.id
                )

        findNavController().navigate(directions = action, navigatorExtras = extras)
    }

    private fun onFavClick(favorite: com.rick.data.model_movie.tmdb.search.Search) {
        viewModel.onEvent(SearchUiEvent.InsertFavorite(favorite))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

class SearchAdapter(
    private val onItemClicked: (view: View, movie: com.rick.data.model_movie.tmdb.search.Search) -> Unit,
    private val onFavClicked: (favorite: com.rick.data.model_movie.tmdb.search.Search) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {

    private val searchDiffUtil = object : DiffUtil.ItemCallback<com.rick.data.model_movie.tmdb.search.Search>() {
        override fun areItemsTheSame(
            oldItem: com.rick.data.model_movie.tmdb.search.Search,
            newItem: com.rick.data.model_movie.tmdb.search.Search
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: com.rick.data.model_movie.tmdb.search.Search,
            newItem: com.rick.data.model_movie.tmdb.search.Search
        ): Boolean {
            return oldItem == newItem
        }
    }

    val searchDiffer = AsyncListDiffer(this, searchDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.create(parent, onItemClicked, onFavClicked)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val searchResult = searchDiffer.currentList[position]
        (holder).bind(searchResult)
    }

    override fun getItemCount(): Int =
        searchDiffer.currentList.size
}

class SearchViewHolder(
    binding: MovieEntryBinding,
    private val onItemClicked: (view: View, movie: com.rick.data.model_movie.tmdb.search.Search) -> Unit,
    private val onFavClicked: (favorite: com.rick.data.model_movie.tmdb.search.Search) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.movieImage
    private val title = binding.movieName
    private val description = binding.movieSummary
    private val rootLayout = binding.movieEntryCardView

    init {
        binding.root.setOnClickListener {
            onItemClicked(it, result)
        }
        binding.favButton.setOnClickListener {
            onFavClicked(result)
        }
    }

    private lateinit var result: com.rick.data.model_movie.tmdb.search.Search

    fun bind(searchResult: com.rick.data.model_movie.tmdb.search.Search) {
        this.rootLayout.transitionName = "search ${searchResult.id}"
        this.result = searchResult
        this.title.text = searchResult.title
        Log.e("Tagg", "Ttle: ${searchResult.title}")
        this.description.text = searchResult.summary
        if (!searchResult.image.isNullOrBlank()) provideGlide(this.image, getTmdbImageUrl(searchResult.image))
    }

//    overridee fun onClick(v: View) {
//        onItemClicked(v, searchResult)
//    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClicked: (view: View, movie: com.rick.data.model_movie.tmdb.search.Search) -> Unit,
            onFavClicked: (favorite: com.rick.data.model_movie.tmdb.search.Search) -> Unit
        ): SearchViewHolder {
            val itemBinding = MovieEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return SearchViewHolder(itemBinding, onItemClicked, onFavClicked)
        }
    }
}