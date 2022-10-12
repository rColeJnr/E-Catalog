package com.rick.screen_anime.manga_screen

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
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
    private lateinit var navController: NavController

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

        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        view?.findViewById<Toolbar>(R.id.toolbar)
            ?.setupWithNavController(navController, appBarConfiguration)

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
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        val action =
            MangaCatalogFragmentDirections.actionMangaCatalogFragmentToDetailsAnimeFragment(
                anime = null, manga = manga
            )
        navController.navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.jikan_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_jikan -> {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                    duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
                }
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                    duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
                }
                val action = MangaCatalogFragmentDirections
                    .actionMangaCatalogFragmentToSearchAnimeFragment()
                navController.navigate(action)
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