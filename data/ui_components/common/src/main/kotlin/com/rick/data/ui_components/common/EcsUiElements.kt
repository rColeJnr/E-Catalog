package com.rick.data.ui_components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EcsAnimatedVisibilityBox(
    screenState: Boolean,
    density: Density,
    fromTop: Dp = 0.dp,
    modifier: Modifier,
    composable: @Composable () -> Unit
) {
    AnimatedVisibility(visible = screenState, enter = slideInVertically {
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
fun EcsText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 22.sp,
    maxLines: Int = 5
) {
    Text(
        text = text,
        maxLines = maxLines,
        fontSize = fontSize,
        overflow = TextOverflow.Ellipsis,
        fontFamily = FontFamily(Font(R.font.high_tower_text, FontWeight.Normal)),
        textAlign = TextAlign.Start,
        color = colorResource(id = R.color.data_ui_components_common_text),
        modifier = modifier.padding(bottom = 2.dp)
    )
}

@Composable
fun EcsTextSmaller(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 14.sp,
    maxLines: Int = 1
) {
    Text(
        text = text,
        maxLines = maxLines,
        fontSize = fontSize,
        overflow = TextOverflow.Ellipsis,
        fontFamily = FontFamily(Font(R.font.high_tower_text, FontWeight.Light)),
        textAlign = TextAlign.Start,
        color = Color.Gray,
        modifier = modifier.padding(vertical = 1.dp)
    )
}

@Composable
fun EcsTextButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
//        colors = ButtonDefaults.textButtonColors(containerColor = colorResource(id = R.color.data_ui_components_common_background))
    ) {
        EcsText(text = text, fontSize = 15.sp)
    }
}

@Composable
fun EcsSectionRow(text: String, isVisible: Boolean, onClick: (Boolean) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(!isVisible) }
            .padding(start = 6.dp),
    ) {
        EcsText(
            text = text, fontSize = 28.sp, modifier = Modifier
        )
        Icon(
            imageVector = if (isVisible) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun EcsEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .testTag("bookmarks:empty"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.data_ui_components_common_app_icon),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(id = R.string.data_ui_components_common_favorites),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.data_ui_components_common_nothing_found),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun EcsScaffold(
    snackbarHostState: SnackbarHostState,
    screenContent: @Composable () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),

        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = BackgroundGradient
                )
        ) {
            screenContent()
        }
    }
}

@Composable
fun ErrorMessage(message: String, onClick: () -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        EcsText(
            text = message, modifier = Modifier
        )
        TextButton(
            onClick = { onClick() },
        ) {
            EcsText(modifier = Modifier, text = "Retry")
        }
    }
}

private val BackgroundGradient = linearGradient(
    colors = listOf(
        Color(0xFFD4D4D4), // startColor
        Color(0xFFFFFFFF)  // endColor
    ),
    // 315 degrees corresponds to Top-Right to Bottom-Left
    start = Offset.Infinite,
    end = Offset.Zero
)
