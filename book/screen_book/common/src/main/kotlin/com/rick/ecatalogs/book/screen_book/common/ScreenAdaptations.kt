package com.rick.ecatalogs.book.screen_book.common

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rick.ecatalogs.data.ui_components.common.EcsPosterItem
import com.rick.ecatalogs.data.ui_components.common.EcsHeroCarousel
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import com.rick.ecatalogs.movie.data_movie.data.repository.browse.TmdbBrowseRepository
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.TmdbTitle
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** Deep link to a TMDB movie or series details screen. */
fun TmdbTitle.detailsUri(): Uri = Uri.parse(
    if (isMovie) "com.rick.ecs://trending_movie_details_fragment/$id"
    else "com.rick.ecs://trending_series_details_fragment/$id"
)

fun TmdbTitle.asPosterItem() = EcsPosterItem(
    id = id,
    title = title,
    imageUrl = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
    subtitle = year?.toString(),
)

/**
 * Films and series adapted from a book (TMDB titles matching the book's title). Loads when
 * shown; hidden entirely when [showWhenEmpty] is false and nothing is found.
 */
@Composable
fun ScreenAdaptations(
    bookTitle: String,
    findAdaptations: suspend (String) -> List<TmdbTitle>,
    onClick: (TmdbTitle) -> Unit,
    showWhenEmpty: Boolean = false,
) {
    var adaptations by remember(bookTitle) { mutableStateOf<List<TmdbTitle>?>(null) }
    var failed by remember(bookTitle) { mutableStateOf(false) }
    var attempt by remember(bookTitle) { mutableIntStateOf(0) }
    LaunchedEffect(bookTitle, attempt) {
        failed = false
        adaptations = try {
            findAdaptations(bookTitle)
        } catch (e: Exception) {
            failed = true
            null
        }
    }
    val title = stringResource(R.string.book_screen_book_common_adaptations)
    val found = adaptations
    when {
        found != null && found.isNotEmpty() -> EcsHeroCarousel(
            title = title,
            items = found.map { it.asPosterItem() },
            onClick = { item -> found.firstOrNull { it.id == item.id }?.let(onClick) },
            modifier = Modifier.padding(bottom = 12.dp),
        )
        !showWhenEmpty -> Unit
        failed -> Text(
            text = stringResource(R.string.book_screen_book_common_adaptations_error),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { attempt++ }.padding(20.dp),
        )
        found == null -> LinearProgressIndicator(Modifier.fillMaxWidth().padding(20.dp))
        else -> Text(
            text = stringResource(R.string.book_screen_book_common_no_adaptations),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(20.dp),
        )
    }
}

/** Bottom sheet with a book's film and TV adaptations, opened from the reader. */
@AndroidEntryPoint
class ScreenAdaptationsSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var browseRepository: TmdbBrowseRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        val bookTitle = requireArguments().getString(ARG_TITLE).orEmpty()
        setContent {
            EcsTheme {
                Column(Modifier.padding(top = 8.dp, bottom = 24.dp)) {
                    ScreenAdaptations(
                        bookTitle = bookTitle,
                        findAdaptations = browseRepository::screenAdaptations,
                        onClick = { title ->
                            val navController = requireParentFragment().findNavController()
                            dismiss()
                            navController.navigateWithMotion(title.detailsUri())
                        },
                        showWhenEmpty = true,
                    )
                }
            }
        }
    }

    companion object {
        private const val ARG_TITLE = "title"

        fun newInstance(bookTitle: String) = ScreenAdaptationsSheet().apply {
            arguments = bundleOf(ARG_TITLE to bookTitle)
        }
    }
}
