package com.rick.ecatalogs.anime.screen_anime.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanRelation
import com.rick.ecatalogs.data.ui_components.common.EcsSectionTitle

/**
 * "Related": the anime's source manga / the manga's anime adaptation, sequels, prequels…
 * Tapping one opens its details.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JikanRelationsSection(relations: List<JikanRelation>, onClick: (JikanRelation) -> Unit) {
    if (relations.isEmpty()) return
    Column {
        EcsSectionTitle(stringResource(R.string.anime_screen_anime_common_related))
        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            relations.forEach { relation ->
                val type = stringResource(
                    if (relation.isAnime) R.string.anime_screen_anime_common_type_anime
                    else R.string.anime_screen_anime_common_type_manga
                )
                AssistChip(
                    onClick = { onClick(relation) },
                    label = {
                        Text(
                            text = "${relationLabel(relation.relation)} · $type: ${relation.name}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
            }
        }
    }
}

/** Jikan's English relation labels, translated where known. */
@Composable
private fun relationLabel(relation: String): String = when (relation.lowercase()) {
    "adaptation" -> stringResource(R.string.anime_screen_anime_common_relation_adaptation)
    "sequel" -> stringResource(R.string.anime_screen_anime_common_relation_sequel)
    "prequel" -> stringResource(R.string.anime_screen_anime_common_relation_prequel)
    "side story" -> stringResource(R.string.anime_screen_anime_common_relation_side_story)
    "spin-off" -> stringResource(R.string.anime_screen_anime_common_relation_spin_off)
    "alternative version" -> stringResource(R.string.anime_screen_anime_common_relation_alternative_version)
    "parent story" -> stringResource(R.string.anime_screen_anime_common_relation_parent_story)
    "summary" -> stringResource(R.string.anime_screen_anime_common_relation_summary)
    else -> relation
}

/** Deep link to an anime's or manga's details screen. */
fun jikanDetailsUri(id: Int, isAnime: Boolean): android.net.Uri =
    android.net.Uri.parse(
        if (isAnime) "com.rick.ecs://anime_details_fragment/$id"
        else "com.rick.ecs://manga_details_fragment/$id"
    )
