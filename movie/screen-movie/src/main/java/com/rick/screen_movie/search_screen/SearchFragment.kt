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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentSearchBinding
import com.rick.screen_movie.databinding.SearchEntryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

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
            requireActivity(), list = listOf(
                IMDBSearchResult(
                    id = "tt0122933", description = "(1999)",
                    image = "https://m.media-amazon.com/images/M/MV5BNTIzNjQ5OTMtM2U2NS00YTNkLWIzMmYtOTcyOTA5MDU0YTdjXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_Ratio0.7273_AL_.jpg",
                    resultType = "CaseMap.Title", title = "Analyze This"
                ),
                IMDBSearchResult(
                    id = "tt0122934", description = "(2000)",
                    image = "https://m.media-amazon.com/images/M/MV5BNTIzNjQ5OTMtM2U2NS00YTNkLWIzMmYtOTcyOTA5MDU0YTdjXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_Ratio0.7273_AL_.jpg",
                    resultType = "CaseMap.Title", title = "Analyze This"
                )
            )
        )

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = searchAdapter

//        binding.bindState(
//            listData = listOf(
//                IMDBSearchResult(
//                    id = "tt0122933", description = "(1999)",
//                    image = "https://m.media-amazon.com/images/M/MV5BNTIzNjQ5OTMtM2U2NS00YTNkLWIzMmYtOTcyOTA5MDU0YTdjXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_Ratio0.7273_AL_.jpg",
//                    resultType = "CaseMap.Title", title = "Analyze This"
//                ),
//                IMDBSearchResult(
//                    id = "tt0122934", description = "(2000)",
//                    image = "https://m.media-amazon.com/images/M/MV5BNTIzNjQ5OTMtM2U2NS00YTNkLWIzMmYtOTcyOTA5MDU0YTdjXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_Ratio0.7273_AL_.jpg",
//                    resultType = "CaseMap.Title", title = "Analyze This"
//                )
//            ),
//            uiAction = viewModel.searchAction,
//            uiState = viewModel.searchState,
//        )

        return binding.root
    }

    private fun FragmentSearchBinding.bindState(
        listData: List<IMDBSearchResult>,
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
        searchInput.text!!.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(SearchUiAction.SearchMovie(title = it.toString()))
            }
        }
    }

    private fun FragmentSearchBinding.bindList(
        adapter: SearchAdapter,
        listData: List<IMDBSearchResult>
    ) {
//        adapter.searchDiffer.submitList(
//            listData
//        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

class SearchAdapter(private val activity: Activity, private val list: List<IMDBSearchResult>) :
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
        Log.d("TAGG", "FADF")
        val itemBinding = SearchEntryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val searchResult = list[position]
        (holder).bind(searchResult, activity = activity)
    }

    override fun getItemCount(): Int =
        list.size
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
        Log.d("DATA", "TAGG ${searchResult.title} and ${this.title.text}")
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