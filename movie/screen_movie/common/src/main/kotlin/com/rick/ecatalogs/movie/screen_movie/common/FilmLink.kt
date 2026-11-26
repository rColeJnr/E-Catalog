package com.rick.ecatalogs.movie.screen_movie.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * "See the film" under a movie review: looks the reviewed film up on TMDB when tapped (so
 * opening a review costs nothing), then opens it, or says it couldn't be found.
 */
@Composable
fun FilmLinkButton(findFilmId: suspend () -> Int?, onOpenFilm: (Int) -> Unit) {
    val scope = rememberCoroutineScope()
    var searching by remember { mutableStateOf(false) }
    var notFound by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
        when {
            searching -> CircularProgressIndicator(Modifier.size(24.dp).padding(2.dp))
            notFound -> Text(
                text = stringResource(R.string.movie_screen_movie_common_film_not_found),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(8.dp),
            )
            else -> TextButton(onClick = {
                searching = true
                scope.launch {
                    val id = runCatching { findFilmId() }.getOrNull()
                    searching = false
                    if (id != null) onOpenFilm(id) else notFound = true
                }
            }) {
                Text(stringResource(R.string.movie_screen_movie_common_see_the_film))
            }
        }
    }
}

/**
 * The film named in an NYT review headline, e.g. "‘Wicked: For Good’ Review: Defying…" ->
 * "Wicked: For Good". Null when the headline doesn't name it in quotes or before "Review".
 */
fun filmTitleFromReviewHeadline(headline: String): String? {
    val quoted = Regex("^\\s*[‘“\"']([^’”\"']+)[’”\"']").find(headline)?.groupValues?.get(1)
    val beforeReview = Regex("^(.+?)\\s+Review\\b").find(headline)?.groupValues?.get(1)
        ?.trim()?.trim('‘', '’', '“', '”', '"', '\'')
    return (quoted ?: beforeReview)?.trim()?.takeIf { it.isNotEmpty() }
}
