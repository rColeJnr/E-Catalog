//package com.rick.book.screen_book.bestseller_search
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.view.doOnPreDraw
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.asLiveData
//import androidx.navigation.fragment.FragmentNavigatorExtras
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.DividerItemDecoration
//import com.google.android.material.transition.MaterialElevationScale
//import com.google.android.material.transition.MaterialSharedAxis
//import com.rick.data.model_book.gutenberg.Formats
//import com.rick.screen_book.databinding.FragmentBookSearchBinding
//import com.rick.screen_book.gutenberg_screen.BookCatalogFragmentDirections
//import com.rick.book.screen_book.bestseller_search.GutenbergSearchUiEvent
//import com.rick.book.screen_book.bestseller_search.GutenbergSearchUiState
//import com.rick.ui_components.common.RecentSearchesBody
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class SearchFragment : Fragment() {
//
//    private var _binding: FragmentBookSearchBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: GutenbergSearchViewModel by viewModels()
//    private lateinit var adapter: GutenbergSearchAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
//            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
//        }
//        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
//            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentBookSearchBinding.inflate(inflater, container, false)
//
//        binding.toolbar.apply {
//            inflateMenu(R.menu.book_search_menu)
//
////            menu.findItem(R.id.fav_book).isVisible = false
//
//            setOnMenuItemClickListener { item ->
//                when (item.itemId) {
//                    R.id.search_books -> {
//                        binding.updateListFromInput(
//                            onQueryChanged = {
//                                viewModel.onEvent(
//                                    GutenbergSearchUiEvent.SearchQuery(it)
//                                )
//                            }
//                        )
//                        true
//                    }
//
//                    else -> super.onOptionsItemSelected(item)
//                }
//            }
//
//            setNavigationIcon(R.drawable.ic_arrow_back)
//            setNavigationOnClickListener {
//                findNavController().navigateUp()
//            }
//        }
//
//        initAdapter()
//
//
//        binding.bindState(
//            onQueryChanged = {
//                viewModel.onEvent(GutenbergSearchUiEvent.SearchQuery(it))
//                viewModel.onEvent(GutenbergSearchUiEvent.OnSearchTriggered(it))
//            },
//            uiState = viewModel.searchState.asLiveData(),
//            recentSearchesUiState = viewModel.recentSearchState.asLiveData()
//        )
//
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        postponeEnterTransition()
//        view.doOnPreDraw { startPostponedEnterTransition() }
//    }
//
//    private fun initAdapter() {
//        adapter = GutenbergSearchAdapter(
//            this::onBookClick,
//            this::onFavClick
//        )
//
//        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
//        binding.list.addItemDecoration(decoration)
//    }
//
//    // TDDO
//    private fun onFavClick(favorite: Int, isFavorite: Boolean) {
//        viewModel.onEvent(GutenbergSearchUiEvent.UpdateFavorite(favorite, isFavorite))
//    }
//
//    private fun FragmentBookSearchBinding.bindState(
//        onQueryChanged: (String) -> Unit,
//        uiState: LiveData<GutenbergSearchUiState>,
//        recentSearchesUiState: LiveData<GutenbergRecentSearchQueriesUiState>
//    ) {
//
//        list.adapter = adapter
//
//        bindSearch(
//            onQueryChanged = onQueryChanged
//        )
//
//        bindList(
//            adapter = adapter,
//            uiState = uiState,
//            recentSearchesUiState = recentSearchesUiState,
//            onClearRecentSearches = { viewModel.onEvent(GutenbergSearchUiEvent.ClearRecentSearches) },
//            onRecentSearchClicked = {
//                viewModel.onEvent(GutenbergSearchUiEvent.SearchQuery(it))
//                viewModel.onEvent(GutenbergSearchUiEvent.OnSearchTriggered(it))
//            }
//        )
//    }
//
//    private fun FragmentBookSearchBinding.bindSearch(
//        onQueryChanged: (String) -> Unit,
//    ) {
//
////        showSoftKeyboard(searchInput, requireContext())
//
//        searchInput.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO) {
//                updateListFromInput(onQueryChanged)
//                true
//            } else {
//                false
//            }
//        }
//        searchInput.setOnKeyListener { _, keyCode, event ->
//            if (event.action == android.view.KeyEvent.ACTION_DOWN && keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
//                updateListFromInput(onQueryChanged)
//                true
//            } else {
//                false
//            }
//        }
//    }
//
//    private fun FragmentBookSearchBinding.updateListFromInput(onQueryChanged: (String) -> Unit) {
//        searchInput.text!!.trim().let { query ->
//            if (query.isNotEmpty()) {
//                list.scrollToPosition(0)
//                onQueryChanged(query.toString())
//            }
//        }
//    }
//
//    private fun FragmentBookSearchBinding.bindList(
//        adapter: GutenbergSearchAdapter,
//        uiState: LiveData<GutenbergSearchUiState>,
//        recentSearchesUiState: LiveData<GutenbergRecentSearchQueriesUiState>,
//        onClearRecentSearches: () -> Unit,
//        onRecentSearchClicked: (String) -> Unit,
//    ) {
//
//        uiState.observe(viewLifecycleOwner) { state ->
//            when (state) {
//                GutenbergSearchUiState.EmptyQuery -> {
//                    recentSearchesUiState.observe(viewLifecycleOwner) { state ->
//                        if (state is GutenbergRecentSearchQueriesUiState.Success) {
//                            recentSearchesComposeView.visibility = View.VISIBLE
//                            recentSearchesComposeView.setContent {
//                                RecentSearchesBody(
//                                    onClearRecentSearches = onClearRecentSearches,
//                                    onRecentSearchClicked = onRecentSearchClicked,
//                                    recentSearchQueries = state.recentQueries.map { it.query },
//                                )
//                            }
//                        }
//                    }
//                }
//
//                GutenbergSearchUiState.Error -> {
//                    if (adapter.differ.currentList.isEmpty()) {
//
//                        searchErrorMessage.visibility = View.VISIBLE
//                    } else {
//                        searchErrorMessage.visibility = View.GONE
//                    }
//                }
//
//                GutenbergSearchUiState.Loading -> searchProgressBar.visibility = View.VISIBLE
//                is GutenbergSearchUiState.Success -> {
//                    recentSearchesComposeView.visibility = View.GONE
//                    searchProgressBar.visibility = View.GONE
//                    adapter.differ.submitList(state.gutenbergs)
//                }
//            }
//        }
//    }
//
//    private fun onBookClick(view: View, formats: Formats) {
//        exitTransition = MaterialElevationScale(false).apply {
//            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
//        }
//        reenterTransition = MaterialElevationScale(true).apply {
//            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
//        }
//        val searchToDetails = getString(R.string.search_transition_name, formats.imageJpeg)
//        val extras = FragmentNavigatorExtras(view to searchToDetails)
//        val link: String? = formats.run {
//            this.textPlain ?: this.textHtml ?: this.textPlainCharsetUtf8
//        }
//        link?.let {
//            val action =
//                BookCatalogFragmentDirections.actionBookCatalogFragmentToBookDetailsFragment(link)
//            findNavController().navigate(directions = action, navigatorExtras = extras)
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
//
//}
