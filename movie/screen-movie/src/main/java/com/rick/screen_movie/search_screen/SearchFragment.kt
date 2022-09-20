package com.rick.screen_movie.search_screen

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentSearchBinding
import com.rick.screen_movie.databinding.SearchEntryBinding
import com.rick.screen_movie.util.showSoftKeyboard
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

            menu.findItem(R.id.search_imdb).isVisible = false

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.search_options -> {
                        // TODO SHOW OPTIONS
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
            glide,
            options,
            this::onMovieClick
        )

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)
    }

    private fun FragmentSearchBinding.bindState(
        searchList: LiveData<List<IMDBSearchResult>>,
        searchLoading: LiveData<Boolean>,
        searchError: LiveData<String>,
        uiAction: (SearchUiAction) -> Unit,
        uiState: StateFlow<SearchUiState>
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
        uiState: StateFlow<SearchUiState>,
        onQueryChanged: (SearchUiAction.SearchMovie) -> Unit
    ) {

        showSoftKeyboard(searchInput, requireContext())

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

        lifecycleScope.launchWhenCreated {
            uiState
                .map { it.movieQuery }
                .distinctUntilChanged()
                .collectLatest(searchInput::setText)
        }
    }

    private fun FragmentSearchBinding.updateListFromInput(onQueryChanged: (SearchUiAction.SearchMovie) -> Unit) {
        searchInput.text!!.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(SearchUiAction.SearchMovie(title = it.toString()))
            }
        }
    }

    private fun FragmentSearchBinding.bindList(
        adapter: SearchAdapter,
        searchList: LiveData<List<IMDBSearchResult>>,
        searchLoading: LiveData<Boolean>,
        searchError: LiveData<String>
    ) {
        lifecycleScope.launch {
            searchList.observe(viewLifecycleOwner) {
                adapter.searchDiffer.submitList(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            searchLoading.observe(viewLifecycleOwner) {
                if (it) searchProgressBar.visibility = View.VISIBLE
                else searchProgressBar.visibility = View.GONE
            }

            searchError.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    searchErrorMessage.visibility = View.VISIBLE
                } else {
                    searchErrorMessage.visibility = View.GONE
                }
            }
        }
    }

    private fun onMovieClick(view: View, movie: IMDBSearchResult) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        val searchToDetails = getString(R.string.search_transition_name, movie.id)
        val extras = FragmentNavigatorExtras(view to searchToDetails)
        val action = SearchFragmentDirections
            .actionSearchFragmentToMovieDetailsFragment(movieId = movie.id, movieTitle = null)
        findNavController().navigate(directions = action, navigatorExtras = extras)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

class SearchAdapter(
    private val glide: RequestManager,
    private val options: RequestOptions,
    private val onItemClicked: (view: View, movie: IMDBSearchResult) -> Unit
) :
    RecyclerView.Adapter<SearchViewHolder>() {

    private val searchDiffUtil = object : DiffUtil.ItemCallback<IMDBSearchResult>() {
        override fun areItemsTheSame(
            oldItem: IMDBSearchResult,
            newItem: IMDBSearchResult
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: IMDBSearchResult,
            newItem: IMDBSearchResult
        ): Boolean {
            return oldItem == newItem
        }
    }

    val searchDiffer = AsyncListDiffer(this, searchDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.create(parent, onItemClicked)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val searchResult = searchDiffer.currentList[position]
        (holder).bind(glide, options, searchResult)
    }

    override fun getItemCount(): Int =
        searchDiffer.currentList.size
}

class SearchViewHolder(
    binding: SearchEntryBinding,
    private val onItemClicked: (view: View, movie: IMDBSearchResult) -> Unit
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    private val image = binding.image
    private val title = binding.title
    private val description = binding.description
    private val searchLLayout = binding.searchLlayout

    init {
        binding.root.setOnClickListener(this)
    }

    private lateinit var searchResult: IMDBSearchResult

    fun bind(glide: RequestManager, options: RequestOptions, searchResult: IMDBSearchResult) {
        this.searchLLayout.transitionName = "search ${searchResult.id}"
        this.searchResult = searchResult
        this.title.text = searchResult.title
        this.description.text = searchResult.description
        glide
            .load(searchResult.image)
            .apply(options)
            .into(this.image)
    }

    override fun onClick(v: View) {
        onItemClicked(v, searchResult)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClicked: (view: View, movie: IMDBSearchResult) -> Unit
        ): SearchViewHolder {
            val itemBinding = SearchEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return SearchViewHolder(itemBinding, onItemClicked)
        }
    }
}