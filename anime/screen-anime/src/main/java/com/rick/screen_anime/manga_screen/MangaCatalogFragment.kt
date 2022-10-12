package com.rick.screen_anime.manga_screen

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import com.rick.data_anime.model_manga.Manga
import com.rick.screen_anime.R
import com.rick.screen_anime.databinding.FragmentMangaCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow

@AndroidEntryPoint
class MangaCatalogFragment : Fragment() {

    private var _binding: FragmentMangaCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MangaCatalogViewModel by viewModels()
    private lateinit var adapter: MangaCatalogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMangaCatalogBinding.inflate(inflater, container, false)

        initAdapter()

        binding.bindList(
            viewModel.pagingDataFlow
        )

        return binding.root
    }

    private fun initAdapter() {
        adapter = MangaCatalogAdapter(
            this::onMangaClick
        )

        binding.recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun FragmentMangaCatalogBinding.bindList(pagingDataFlow: Flow<PagingData<Manga>>) {

        lifecycleScope.launchWhenCreated {
            pagingDataFlow.collect(adapter::submitData)
        }

    }

    private fun onMangaClick(view: View, manga: Manga) {

        val action =
            MangaCatalogFragmentDirections.actionMangaCatalogFragmentToDetailsAnimeFragment(
                anime = null, manga = manga
            )
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.jikan_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_jikan -> {
                val action = MangaCatalogFragmentDirections
                    .actionMangaCatalogFragmentToSearchAnimeFragment()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}