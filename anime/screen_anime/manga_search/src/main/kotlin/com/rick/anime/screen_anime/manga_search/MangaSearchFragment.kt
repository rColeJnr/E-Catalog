package com.rick.anime.screen_anime.manga_search

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
        _binding = AnimeScreenAnimeMangaSearchFragmentMangaSearchBinding.inflate(
            inflater,
            container,
            false
        )

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

            setNavigationIcon(R.drawable.anime_screen_anime_manga_search_ic_arrow_back)
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
        val circularProgressDrawable = CircularProgressDrawable(requireContext()).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }
        val options = RequestOptions().placeholder(circularProgressDrawable)
        val glide = Glide.with(requireContext())
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

        bindList(adapter = searchAdapter,
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
                resources.getInteger(R.integer.anime_screen_anime_manga_search_motion_duration_long)
                    .toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration =
                resources.getInteger(R.integer.anime_screen_anime_manga_search_motion_duration_long)
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
//
//class MangaSearchAdapter(
//    private val onItemClicked: (View, UserManga) -> Unit,
//    private val onFavClicked: (Int, Boolean) -> Unit
//) : RecyclerView.Adapter<MangaViewHolder>() {
//
//    private val searchDiffUtil = object : DiffUtil.ItemCallback<UserManga>() {
//        override fun areItemsTheSame(
//            oldItem: UserManga, newItem: UserManga
//        ): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(
//            oldItem: UserManga, newItem: UserManga
//        ): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    val searchDiffer = AsyncListDiffer(this, searchDiffUtil)
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup, viewType: Int
//    ): MangaViewHolder {
//        return MangaViewHolder.create(parent, onItemClicked, onFavClicked)
//    }
//
//    override fun onBindViewHolder(
//        holder: MangaViewHolder,
//        position: Int
//    ) {
//        val searchResult = searchDiffer.currentList[position]
//        (holder).bind(searchResult)
//    }
//
//    override fun getItemCount(): Int = searchDiffer.currentList.size
//}
//
//class MangaSearchViewHolder(
//    binding: AnimeScreenAnimeJikanEntryBinding,
//    private val onItemClicked: (view: View, UserManga) -> Unit,
//    private val onFavClicked: (Int, Boolean) -> Unit
//) : RecyclerView.ViewHolder(binding.root) {
//    private val image = binding.image
//    private val title = binding.title
//    private val description = binding.synopsis
//    private val rootLayout = binding.root
//    private val favorite = binding.favButton
//    private val resources = itemView.resources
//
//    init {
//        binding.root.setOnClickListener {
//            onItemClicked(it, searchResult)
//        }
//        favorite.setOnClickListener {
//            onFavClicked(searchResult.id, searchResult.isFavorite)
//        }
//    }
//
//    private lateinit var searchResult: UserManga
//
//    fun bind(searchResult: UserManga) {
//        this.rootLayout.transitionName = "search ${searchResult.id}"
//        this.searchResult = searchResult
//        this.title.text = searchResult.title
//        this.description.text = searchResult.synopsis
//        provideGlide(
//            this.image, getTmdbImageUrl(searchResult.images)
//        )
//        favorite.foreground = if (searchResult.isFavorite) {
//            ResourcesCompat.getDrawable(resources, R.drawable.fav_filled_icon, null)
//        } else {
//            ResourcesCompat.getDrawable(resources, R.drawable.fav_outline_icon, null)
//        }
//    }
//
////    overridee fun onClick(v: View) {
////        onItemClicked(v, searchResult)
////    }
//
//    companion object {
//        fun create(
//            parent: ViewGroup,
//            onItemClicked: (View, UserManga) -> Unit,
//            onFavClicked: (Long, Boolean) -> Unit
//        ): ArticleSearchViewHolder {
//            val itemBinding =
//                AnimeScreenAnimeJikanEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            return ArticleSearchViewHolder(
//                itemBinding, onItemClicked, onFavClicked
//            )
//        }
//    }
//}