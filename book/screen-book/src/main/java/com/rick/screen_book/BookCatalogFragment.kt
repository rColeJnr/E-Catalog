package com.rick.screen_book

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.accompanist.themeadapter.material.MdcTheme
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.data_book.favorite.Favorite
import com.rick.data_book.gutenberg.model.Book
import com.rick.data_book.gutenberg.model.Formats
import com.rick.screen_book.databinding.FragmentBookCatalogBinding
import com.rick.screen_book.favorite_screen.FavoriteEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BookCatalogFragment : Fragment() {

    private var _binding: FragmentBookCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookCatalogViewModel by viewModels()
    private lateinit var adapter: BookCatalogAdapter
    private lateinit var navController: NavController

    private lateinit var eTransition: MaterialSharedAxis
    private lateinit var reTransition: MaterialSharedAxis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookCatalogBinding.inflate(inflater, container, false)

        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        view?.findViewById<Toolbar>(R.id.toolbar)
            ?.setupWithNavController(navController, appBarConfiguration)

        initAdapter()

        binding.bindList(
            adapter = adapter,
            viewModel.pagingDataFlow
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        reTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

    }

    private fun initAdapter() {
        adapter = BookCatalogAdapter(this::onBookClick, this::onFavClick)

        binding.recyclerView.adapter =
            adapter.withLoadStateFooter(footer = BookLoadStateAdapter { adapter.retry() })

        binding.recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun FragmentBookCatalogBinding.bindList(
        adapter: BookCatalogAdapter,
        pagingDataFlow: Flow<PagingData<Book>>
    ) {

        lifecycleScope.launch {
            pagingDataFlow.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                // show progress bar during initial load or refresh.
                swipeRefresh.isRefreshing = loadState.mediator?.refresh is LoadState.Loading
                // show empty list.
                composeView.setContent {
                    MdcTheme {
                        ErrorMessage(getString(R.string.no_results))
                    }
                }
                composeView.isVisible =
                    !swipeRefresh.isRefreshing && adapter.itemCount == 0

                val errorState = loadState.source.refresh as? LoadState.Error
                    ?: loadState.mediator?.refresh as? LoadState.Error

                errorState?.let {
                    Toast.makeText(
                        context,
                        "\uD83D\uDE28 Wooops $it",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val notLoading = adapter.loadStateFlow.asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        lifecycleScope.launch {
            notLoading.collectLatest {
                if (it) recyclerView.scrollToPosition(0)
            }
        }

        swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun onBookClick(view: View, formats: Formats) {
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        val bookToDetails = getString(R.string.book_transition_name, formats.image)
        val extras = FragmentNavigatorExtras(view to bookToDetails)
        val link: String = formats.run {
            this.textPlain ?: this.textPlainCharsetUtf8 ?: this.textHtmCharsetUtf8!!
        }
        val action = BookCatalogFragmentDirections
            .actionBookCatalogFragmentToBookDetailsFragment(link)
        navController.navigate(directions = action, navigatorExtras = extras)
    }

    // TODO(add book to favorite)
    // man this is a lot of work.
    private fun onFavClick(favorite: Favorite) {
        //how do i even begin to do this?? lol
        // we need to call view model and add the favorite to  db
        // at some point we'LL need to call the db for favorites
        // and present them in the same ui as the remote data.  this sounds like
        // loads of fun.
        // It's was way too much work to do this.  I'm doing something else.
        viewModel.onEvent(FavoriteEvents.InsertFavorite(favorite))

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.book_search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_books -> {

                exitTransition = eTransition
                reenterTransition = reTransition

                navController.navigate(
                    BookCatalogFragmentDirections.actionBookCatalogFragmentToSearchFragment()
                )
                true
            }
//            R.id.fav_book -> {
//
//                exitTransition = eTransition
//                reenterTransition = reTransition
//
//                navController.navigate(
//                    BookCatalogFragmentDirections.actionBookCatalogFragmentToBookFavoritesFragment()
//                )
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .wrapContentSize(align = Alignment.Center),
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun ErrorMessagePreview() {
    MaterialTheme {
        ErrorMessage("No results \uD83D\uDE13\\nSwipe to refresh")
    }
}