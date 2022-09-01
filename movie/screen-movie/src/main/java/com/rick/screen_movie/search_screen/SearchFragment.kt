package com.rick.screen_movie.search_screen

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentSearchBinding
import com.rick.screen_movie.databinding.SearchEntryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchAdapter

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

        searchAdapter = SearchAdapter(
            requireActivity()
        )

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)

        binding.bindState(
            listData = viewModel.searchList,
            uiAction = viewModel.searchAction,
            uiState = viewModel.searchState,
        )

        return binding.root
    }

    private fun FragmentSearchBinding.bindState(
        listData: Flow<List<IMDBSearchResult>>,
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
            listData = listData
        )
    }

    private fun FragmentSearchBinding.bindSearch(
        uiState: StateFlow<SearchUiState>,
        onQueryChanged: (SearchUiAction.SearchMovie) -> Unit
    ) {
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
        viewModel.searchMovies(searchInput.text.toString())
        searchInput.text!!.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(SearchUiAction.SearchMovie(title = it.toString()))
            }
        }
    }

    private fun FragmentSearchBinding.bindList(
        adapter: SearchAdapter,
        listData: Flow<List<IMDBSearchResult>>
    ) {
        lifecycleScope.launch{
            Log.d("TAG", "list -> $listData")
            listData.collect(adapter.searchDiffer::submitList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

class SearchAdapter(private val activity: Activity) :
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
        return SearchViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val searchResult = searchDiffer.currentList[position]
        (holder).bind(searchResult, activity = activity)
    }

    override fun getItemCount(): Int =
        searchDiffer.currentList.size
}

class SearchViewHolder(
    binding: SearchEntryBinding,
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.image
    private val title = binding.title
    private val description = binding.description

    private lateinit var searchResult: IMDBSearchResult

    fun bind(searchResult: IMDBSearchResult, activity: Activity) {
        this.searchResult = searchResult
        this.title.text = searchResult.title
        this.description.text = searchResult.description
        // TODO(INITIAlize glide in activity or fragment)
        Glide.with(activity)
            .load(searchResult.image)
            .into(this.image)
    }

    companion object {
        fun create(parent: ViewGroup): SearchViewHolder {
            val itemBinding = SearchEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return SearchViewHolder(itemBinding)
        }
    }
}