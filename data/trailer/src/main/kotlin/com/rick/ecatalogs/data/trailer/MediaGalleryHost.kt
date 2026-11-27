package com.rick.ecatalogs.data.trailer

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.rick.ecatalogs.data.ui_components.common.EcsTheme

/**
 * A [MediaGallery] for XML screens: added to [container] (sized by it), showing what [show] gets.
 * Call [show] whenever the details change; the gallery keeps its page and the trailer its
 * position. Create it in `onCreateView`: it lives as long as the fragment's view.
 *
 * [onFirstImageLoaded] is called once the first page's image is on screen (or failed), for a
 * postponed enter transition.
 */
class MediaGalleryHost(container: ViewGroup, onFirstImageLoaded: () -> Unit = {}) {

    private var items by mutableStateOf(emptyList<GalleryItem>())
    private var title by mutableStateOf("")

    init {
        container.addView(
            ComposeView(container.context).apply {
                // Without an id, its saved state (page, video position) would be keyed by -1
                id = R.id.data_trailer_gallery
                // Disposed with the fragment's view, not when it's detached during a transition
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    EcsTheme {
                        MediaGallery(items, title, Modifier.fillMaxSize(), onFirstImageLoaded)
                    }
                }
            },
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT),
        )
    }

    fun show(items: List<GalleryItem>, title: String) {
        this.title = title
        this.items = items
    }
}
