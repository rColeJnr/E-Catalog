package com.rick.ecatalogs.movie.screen_movie.common

import android.view.ViewGroup
import com.rick.ecatalogs.data.trailer.GalleryItem
import com.rick.ecatalogs.data.trailer.MediaGalleryHost
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.TmdbDetailsExtras
import com.rick.ecatalogs.movie.screen_movie.common.util.getTmdbImageUrl

/**
 * The swipeable header of the movie and series details screens, added to [container]: the trailer
 * first (it plays right there), then the poster, then TMDB's backdrops.
 * See [MediaGalleryHost] for [onFirstImageLoaded].
 */
class DetailsGallery(container: ViewGroup, onFirstImageLoaded: () -> Unit) {

    private val host = MediaGalleryHost(container, onFirstImageLoaded)

    fun bind(title: String, posterPath: String, extras: TmdbDetailsExtras) =
        host.show(detailsGalleryItems(posterPath, extras), title)
}

/** Trailer, poster, backdrops; whatever TMDB has of them. */
fun detailsGalleryItems(posterPath: String, extras: TmdbDetailsExtras): List<GalleryItem> = buildList {
    extras.trailerYoutubeKey?.let { add(GalleryItem.Trailer(it)) }
    if (posterPath.isNotBlank()) add(GalleryItem.Image(getTmdbImageUrl(posterPath)))
    extras.backdrops.forEach { add(GalleryItem.Image(getTmdbImageUrl(it, BACKDROP_SIZE), wide = true)) }
}.distinctBy { it.key }

/** TMDB backdrop width: sharp on phones and tablets, a fraction of "original". */
private const val BACKDROP_SIZE = "w1280"
