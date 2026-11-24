package com.rick.ecatalogs.data.ui_components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/** A poster in an [EcsPosterRow]. */
@Immutable
data class EcsPosterItem(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val subtitle: String? = null,
)

/** A choice in an [EcsChipRow]. */
@Immutable
data class EcsChip(val id: String, val label: String)

/** Section heading, matching the XML screens' section titles. */
@Composable
fun EcsSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
    )
}

/** A titled, horizontally scrolling row of posters. Nothing is shown when [items] is empty. */
@Composable
fun EcsPosterRow(
    title: String,
    items: List<EcsPosterItem>,
    onClick: (EcsPosterItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    Column(modifier) {
        EcsSectionTitle(title)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items, key = { it.id }) { item -> EcsPoster(item, onClick) }
        }
    }
}

@Composable
private fun EcsPoster(item: EcsPosterItem, onClick: (EcsPosterItem) -> Unit) {
    Column(
        Modifier
            .width(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(item) }
            .padding(2.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(12.dp)),
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
        item.subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** A horizontally scrolling row of single-choice filter chips. */
@Composable
fun EcsChipRow(
    chips: List<EcsChip>,
    selectedId: String?,
    onSelect: (EcsChip) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (chips.isEmpty()) return
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chips, key = { it.id }) { chip ->
            FilterChip(
                selected = chip.id == selectedId,
                onClick = { onSelect(chip) },
                label = { Text(chip.label) },
            )
        }
    }
}

@Preview
@Composable
private fun PreviewBrowse() {
    EcsTheme {
        Column {
            EcsChipRow(
                chips = listOf(EcsChip("popular", "Popular"), EcsChip("top", "Top rated")),
                selectedId = "popular",
                onSelect = {},
            )
            EcsPosterRow(
                title = "This season",
                items = listOf(EcsPosterItem(1, "Frieren", null, "★ 9.1")),
                onClick = {},
            )
        }
    }
}
