package com.rick.ecatalogs.data.trailer

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

/**
 * YouTube's IFrame player (android-youtube-player) in Compose. Starts playing [videoId] at
 * [startSeconds] as soon as it's ready, follows the screen's lifecycle (pauses on stop, released
 * with the composition) and pauses while [paused].
 *
 * With [onFullscreenClick], the player shows its fullscreen button and tapping it calls back
 * instead of taking over the window (the caller opens the trailer screen).
 */
@Composable
internal fun YouTubePlayerHost(
    videoId: String,
    startSeconds: Float,
    paused: Boolean,
    onSecond: (Float) -> Unit,
    onError: (PlayerConstants.PlayerError) -> Unit,
    onFullscreenClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnSecond by rememberUpdatedState(onSecond)
    val currentOnError by rememberUpdatedState(onError)
    val currentOnFullscreen by rememberUpdatedState(onFullscreenClick)
    val withFullscreen = onFullscreenClick != null
    var player by remember { mutableStateOf<YouTubePlayer?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val view = LayoutInflater.from(context)
                .inflate(R.layout.data_trailer_player, FrameLayout(context), false) as YouTubePlayerView
            lifecycle.addObserver(view)
            if (withFullscreen) {
                view.addFullscreenListener(object : FullscreenListener {
                    override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                        exitFullscreen()
                        currentOnFullscreen?.invoke()
                    }

                    override fun onExitFullscreen() = Unit
                })
            }
            val options = IFramePlayerOptions.Builder(context)
                .controls(1)
                .fullscreen(if (withFullscreen) 1 else 0)
                .rel(0)
                .build()
            view.initialize(
                object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        player = youTubePlayer
                        youTubePlayer.loadVideo(videoId, startSeconds)
                    }

                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                        currentOnSecond(second)
                    }

                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                        currentOnError(error)
                    }
                },
                true,
                options,
            )
            view
        },
        onRelease = { view ->
            lifecycle.removeObserver(view)
            view.release()
        },
    )

    LaunchedEffect(paused, player) {
        if (paused) player?.pause()
    }
}
