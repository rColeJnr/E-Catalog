package com.rick.ecatalogs.anime.screen_anime.common

import androidx.compose.material3.AssistChip
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.data.ui_components.common.EcsChip
import com.rick.ecatalogs.data.ui_components.common.EcsChipRow
import com.rick.ecatalogs.data.ui_components.common.EcsPosterItem
import com.rick.ecatalogs.data.ui_components.common.EcsHeroCarousel
import com.rick.ecatalogs.data.ui_components.common.EcsTheme

/**
 * A Compose header as the first item of a RecyclerView (ConcatAdapter in front of a paged list),
 * so new UI can be written in Compose on the existing View-based screens.
 */
class ComposeHeaderAdapter(
    private val content: @Composable () -> Unit,
) : RecyclerView.Adapter<ComposeHeaderAdapter.ViewHolder>() {

    class ViewHolder(val view: ComposeView) : RecyclerView.ViewHolder(view)

    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ComposeView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.setContent { EcsTheme { content() } }
    }
}

/** A titled poster row for [JikanBrowseHeader]. */
data class JikanBrowseRow(val title: String, val items: List<EcsPosterItem>)

/**
 * Top of the anime / manga lists: title and slogan, browse rows, then the list chips (popular,
 * top rated…) and the genre chips. "All genres" has the chip id [ALL_GENRES].
 */
@Composable
fun JikanBrowseHeader(
    title: String,
    slogan: String,
    rows: List<JikanBrowseRow>,
    lists: List<EcsChip>,
    selectedList: String,
    genres: List<EcsChip>,
    selectedGenre: String,
    onItemClick: (EcsPosterItem) -> Unit,
    onListSelected: (String) -> Unit,
    onGenreSelected: (String) -> Unit,
    actions: List<JikanHeaderAction> = emptyList(),
) {
    Column(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = slogan,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        )
        if (actions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                actions.forEach { action ->
                    AssistChip(
                        onClick = action.onClick,
                        enabled = action.enabled,
                        label = { Text(action.label) },
                    )
                }
            }
        }
        // A single horizontal row per screen: the first row that has items
        rows.firstOrNull { it.items.isNotEmpty() }?.let { row ->
            EcsHeroCarousel(row.title, row.items, onItemClick, height = 210.dp)
        }
        Spacer(Modifier.height(12.dp))
        EcsChipRow(lists, selectedList, onSelect = { onListSelected(it.id) })
        EcsChipRow(genres, selectedGenre, onSelect = { onGenreSelected(it.id) })
    }
}

const val ALL_GENRES = "all"

/** A button at the top of the header ("Airing schedule", "Surprise me"). */
data class JikanHeaderAction(val label: String, val enabled: Boolean = true, val onClick: () -> Unit)
