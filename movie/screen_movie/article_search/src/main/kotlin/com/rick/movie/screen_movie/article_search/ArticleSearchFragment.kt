package com.rick.movie.screen_movie.article_search

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
import com.rick.data.model_movie.UserArticle
import com.rick.data.ui_components.common.RecentSearchesBody
import com.rick.movie.screen_movie.article_search.databinding.MovieScreenMovieArticleSearchFragmentArticleSearchBinding
import com.rick.movie.screen_movie.article_search.databinding.MovieScreenMovieArticleSearchSearchEntryBinding
import com.rick.movie.screen_movie.common.ArticleDetailsDialogFragment
import com.rick.movie.screen_movie.common.util.provideGlide
import com.rick.screen_movie.util.getTmdbImageUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleSearchFragment : Fragment() {

    private var _binding: MovieScreenMovieArticleSearchFragmentArticleSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArticleSearchViewModel by viewModels()
    private lateinit var searchAdapter: ArticleSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration =
                resources.getInteger(R.integer.movie_screen_movie_article_search_motion_duration_long)
                    .toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(R.integer.movie_screen_movie_article_search_motion_duration_long)
                    .toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MovieScreenMovieArticleSearchFragmentArticleSearchBinding.inflate(
            inflater,
            container,
            false
        )

        binding.toolbar.apply {
            inflateMenu(R.menu.movie_screen_movie_article_search_menu)

//            menu.findItem(R.id.fav_imdb).isVisible = false

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.search -> {
                        binding.updateListFromInput(onQueryChanged = {
                            viewModel.onEvent(
                                ArticleSearchUiEvent.SearchQuery(it)
                            )
                        })
                        true
                    }

                    else -> super.onOptionsItemSelected(item)
                }
            }

            setNavigationIcon(R.drawable.movie_screen_movie_article_search_ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        initAdapter()

        binding.bindState(
            onQueryChanged = {
                viewModel.onEvent(ArticleSearchUiEvent.SearchQuery(it))
                viewModel.onEvent(ArticleSearchUiEvent.OnSearchTriggered(it))
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
        searchAdapter = ArticleSearchAdapter(
            this::onArticleClick, this::onFavClick
        )

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)
    }

    private fun MovieScreenMovieArticleSearchFragmentArticleSearchBinding.bindState(
        onQueryChanged: (String) -> Unit,
        uiState: LiveData<ArticleSearchUiState>,
        recentSearchesUiState: LiveData<ArticleRecentSearchQueriesUiState>
    ) {

        list.adapter = searchAdapter

        bindSearch(
            onQueryChanged = onQueryChanged
        )

        bindList(
            adapter = searchAdapter,
            uiState = uiState,
            recentSearchesUiState = recentSearchesUiState,
            onClearRecentSearches = { viewModel.onEvent(ArticleSearchUiEvent.ClearRecentSearches) },
            onRecentSearchClicked = {
                viewModel.onEvent(ArticleSearchUiEvent.SearchQuery(it))
                viewModel.onEvent(ArticleSearchUiEvent.OnSearchTriggered(it))
            }
        )
    }

    private fun MovieScreenMovieArticleSearchFragmentArticleSearchBinding.bindSearch(
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

    private fun MovieScreenMovieArticleSearchFragmentArticleSearchBinding.updateListFromInput(
        onQueryChanged: (String) -> Unit
    ) {
        searchInput.text!!.trim().let { query ->
            if (query.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(query.toString())
            }
        }
    }

    private fun MovieScreenMovieArticleSearchFragmentArticleSearchBinding.bindList(
        adapter: ArticleSearchAdapter,
        uiState: LiveData<ArticleSearchUiState>,
        recentSearchesUiState: LiveData<ArticleRecentSearchQueriesUiState>,
        onClearRecentSearches: () -> Unit,
        onRecentSearchClicked: (String) -> Unit,
    ) {

        uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ArticleSearchUiState.EmptyQuery -> {
                    recentSearchesUiState.observe(viewLifecycleOwner) { state ->
                        if (state is ArticleRecentSearchQueriesUiState.Success) {
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

                is ArticleSearchUiState.Error -> {
                    if (adapter.searchDiffer.currentList.isEmpty()) {
                        Log.e(TAG, state.msg)
                        searchErrorMessage.visibility = View.VISIBLE
                    } else {
                        searchErrorMessage.visibility = View.GONE
                    }
                }

                ArticleSearchUiState.Loading -> searchProgressBar.visibility = View.VISIBLE
                is ArticleSearchUiState.Success -> {
                    recentSearchesComposeView.visibility = View.GONE
                    searchProgressBar.visibility = View.GONE
                    adapter.searchDiffer.submitList(state.articles)
                }
            }
        }
    }

    //TODO (REMOVE .toString())
    private fun onArticleClick(view: View, article: UserArticle) {
        ArticleDetailsDialogFragment(
            article,
            this::onDialogFavoriteClick,
            this::onWebUlrClick
        ).show(
            requireActivity().supportFragmentManager,
            "article_details"
        )


    }

    private fun onDialogFavoriteClick(view: View, id: Long, isFavorite: Boolean) {
        onFavClick(id, isFavorite)
    }

    private fun onWebUlrClick(link: String) {
        val uri = Uri.parse("com.rick.ecs://movie_common_webviewfragment//$link")
        findNavController().navigate(uri)
    }

    private fun onFavClick(id: Long, isFavorite: Boolean) {
        viewModel.onEvent(ArticleSearchUiEvent.UpdateFavorite(id, !isFavorite))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

class ArticleSearchAdapter(
    private val onItemClicked: (View, UserArticle) -> Unit,
    private val onFavClicked: (Long, Boolean) -> Unit
) : RecyclerView.Adapter<ArticleSearchViewHolder>() {

    private val searchDiffUtil = object : DiffUtil.ItemCallback<UserArticle>() {
        override fun areItemsTheSame(
            oldItem: UserArticle, newItem: UserArticle
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UserArticle, newItem: UserArticle
        ): Boolean {
            return oldItem == newItem
        }
    }

    val searchDiffer = AsyncListDiffer(this, searchDiffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ArticleSearchViewHolder {
        return ArticleSearchViewHolder.create(parent, onItemClicked, onFavClicked)
    }

    override fun onBindViewHolder(
        holder: ArticleSearchViewHolder,
        position: Int
    ) {
        val searchResult = searchDiffer.currentList[position]
        (holder).bind(searchResult)
    }

    override fun getItemCount(): Int = searchDiffer.currentList.size
}

class ArticleSearchViewHolder(
    binding: MovieScreenMovieArticleSearchSearchEntryBinding,
    private val onItemClicked: (view: View, UserArticle) -> Unit,
    private val onFavClicked: (Long, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.movieImage
    private val title = binding.movieName
    private val description = binding.movieSummary
    private val rootLayout = binding.movieEntryCardView
    private val favorite = binding.favButton
    private val resources = itemView.resources

    init {
        binding.root.setOnClickListener {
            onItemClicked(it, searchResult)
        }
        favorite.setOnClickListener {
            onFavClicked(searchResult.id, searchResult.isFavorite)
        }
    }

    private lateinit var searchResult: UserArticle

    fun bind(searchResult: UserArticle) {
        this.rootLayout.transitionName = "search ${searchResult.id}"
        this.searchResult = searchResult
        this.title.text = searchResult.headline
        this.description.text = searchResult.leadParagraph
        if (searchResult.multimedia.isNotEmpty()) {
            provideGlide(
                this.image, getTmdbImageUrl(searchResult.multimedia)
            )
        }
        favorite.foreground = if (searchResult.isFavorite) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.movie_screen_movie_article_search_ic_fav_filled,
                null
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.movie_screen_movie_article_search_ic_fav_outlined,
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
            onItemClicked: (View, UserArticle) -> Unit,
            onFavClicked: (Long, Boolean) -> Unit
        ): ArticleSearchViewHolder {
            val itemBinding =
                MovieScreenMovieArticleSearchSearchEntryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ArticleSearchViewHolder(
                itemBinding, onItemClicked, onFavClicked
            )
        }
    }
}

private const val TAG = "articleSearchFragment"