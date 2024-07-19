package com.rick.ui_components.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rick.data.ui_components.auth.R


@Composable
fun BulletList(bullets: List<String>) {
    val bullet = "\t\u2022\t"
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
    Text(buildAnnotatedString {
        bullets.forEach {
            withStyle(style = paragraphStyle) {
                append(bullet)
                append(it)
            }
        }
    })
}

@Composable
fun AnimatedVisibilityBox(
    visibility: Boolean,
    density: Density,
    fromTop: Dp = 0.dp,
    modifier: Modifier,
    composable: @Composable () -> Unit
) {
    AnimatedVisibility(visible = visibility, enter = slideInVertically {
        // Slide in from 40 dp from the top.
        with(density) { fromTop.roundToPx() }
    } + expandVertically(
        // Expand from the top.
        expandFrom = Alignment.Top
    ) + fadeIn(
        // Fade in with the initial alpha of 0.3f.
        initialAlpha = 0.3f
    ), exit = slideOutVertically() + shrinkVertically() + fadeOut(), modifier = modifier) {
        composable()
    }
}

@Composable
fun BulletListCard(modifier: Modifier = Modifier, composable: @Composable() () -> Unit) {
    Card(
        modifier = modifier
            .width(dimensionResource(id = R.dimen.data_ui_components_auth_280dp))
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.data_ui_components_auth_white))
    ) {
        Box(modifier = modifier.padding(6.dp)) { composable() }
    }
}

@Composable
fun CrossfadeText(state: String, label: String, padding: Dp) {
    Crossfade(targetState = state, label = label) { text ->
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding),
            text = text,
            textAlign = TextAlign.Center,
            fontSize = integerResource(id = R.integer.data_ui_components_auth_22).sp,
            color = colorResource(id = R.color.data_ui_components_auth_text),
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
fun EcsLeadingIcon(
    visibility: Boolean,
    icon1: Int,
    description1: Int,
    icon2: Int,
    description2: Int,
    onClick: () -> Unit
) {
    val painter =
        if (visibility) painterResource(id = icon1) else painterResource(
            id = icon2
        )

    val description =
        if (visibility) stringResource(description1) else stringResource(
            description2
        )

    IconButton(
        onClick = { onClick() },
    ) {
        Icon(painter = painter, contentDescription = description)
    }
}


@Preview
@Composable
private fun PreviewRequirementsCard() {
    BulletListCard {
        BulletList(bullets = listOf("Ricardo", "Teles", "Nercia"))
    }
}