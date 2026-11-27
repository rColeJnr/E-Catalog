package com.rick.ecatalogs.data.trailer

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/** YouTube's still for a video (480×360, letterboxed to 16:9). */
fun youtubeThumbnailUrl(videoId: String) = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

fun youtubeWatchUrl(videoId: String) = "https://www.youtube.com/watch?v=$videoId"

// /embed/<id>, ?v=<id>, youtu.be/<id> and /shorts/<id>; IDs are 11 characters
private val YOUTUBE_ID = Regex("(?:/embed/|[?&]v=|youtu\\.be/|/shorts/)([A-Za-z0-9_-]{11})(?![A-Za-z0-9_-])")

/** The video ID in a YouTube URL (Jikan gives embed URLs), or null when it isn't one. */
fun youtubeIdFromUrl(url: String?): String? = url?.let { YOUTUBE_ID.find(it)?.groupValues?.get(1) }

/** Opens the video in the YouTube app, or the browser; does nothing when neither is installed. */
fun openInYouTube(context: Context, videoId: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeWatchUrl(videoId)))
    if (context !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // No YouTube app or browser: nothing to open it with
    }
}
