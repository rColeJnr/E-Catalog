package com.rick.ecatalogs.data.trailer

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.rick.ecatalogs.data.ui_components.common.EcsTheme

/**
 * The trailer screen: the video as large as the screen allows, with the title, a close button and
 * "Open in YouTube" in portrait, and immersive full screen in landscape. It handles rotation itself
 * (configChanges in the manifest), so turning the phone never restarts the video.
 */
class TrailerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val videoId = intent.getStringExtra(EXTRA_VIDEO_ID)
        if (videoId.isNullOrBlank()) {
            finish()
            return
        }
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val startSeconds = intent.getFloatExtra(EXTRA_START_SECONDS, 0f)

        setContentView(
            ComposeView(this).apply {
                setContent {
                    EcsTheme(darkTheme = true) {
                        TrailerScreen(videoId, title, startSeconds, onClose = ::finish)
                    }
                }
            }
        )
        applySystemBars(resources.configuration)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applySystemBars(newConfig)
    }

    /** Hides the status and navigation bars in landscape (swipe to show them briefly). */
    private fun applySystemBars(config: Configuration) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    companion object {
        /** The intent's extras: YouTube video id (required), title, start position in seconds. */
        const val EXTRA_VIDEO_ID = "video_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_START_SECONDS = "start_seconds"

        /** Plays YouTube video [videoId] full screen, from [startSeconds]. */
        fun start(context: Context, videoId: String, title: String = "", startSeconds: Float = 0f) {
            context.startActivity(
                Intent(context, TrailerActivity::class.java)
                    .putExtra(EXTRA_VIDEO_ID, videoId)
                    .putExtra(EXTRA_TITLE, title)
                    .putExtra(EXTRA_START_SECONDS, startSeconds)
            )
        }
    }
}

@Composable
private fun TrailerScreen(videoId: String, title: String, startSeconds: Float, onClose: () -> Unit) {
    val context = LocalContext.current
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var failed by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .then(if (landscape) Modifier else Modifier.safeDrawingPadding())
    ) {
        if (!landscape) {
            Row(Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.data_trailer_close), tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    title.ifBlank { stringResource(R.string.data_trailer_trailer) },
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            if (failed) {
                TrailerUnavailable(onOpenInYouTube = { openInYouTube(context, videoId) })
            } else {
                // The same call site in both orientations, so the player survives rotation
                YouTubePlayerHost(
                    videoId = videoId,
                    startSeconds = startSeconds,
                    paused = false,
                    onSecond = {},
                    onError = { failed = true },
                    onFullscreenClick = null,
                    modifier = if (landscape) Modifier.fillMaxHeight().aspectRatio(16f / 9f)
                    else Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                )
            }
        }
        if (!landscape && !failed) {
            OutlinedButton(
                onClick = { openInYouTube(context, videoId) },
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp),
            ) {
                Text(stringResource(R.string.data_trailer_open_in_youtube), color = Color.White)
            }
        }
    }
}
