package com.rick.ecatalogs.data.ui_components.common

import android.widget.ImageView
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/**
 * Static placeholder color. An animated CircularProgressDrawable per item forces a redraw on
 * every frame while images load, which kept the RenderThread busy.
 */
private const val PLACEHOLDER_COLOR = 0x1F808080

/**
 * Loads [src] into [view] with a short crossfade. [onReady] runs once the image is shown or the
 * load fails, e.g. to start a postponed shared-element transition.
 */
fun provideGlide(view: ImageView, src: String, onReady: (() -> Unit)? = null) {
    Glide.with(view)
        .load(src)
        .placeholder(ColorDrawable(PLACEHOLDER_COLOR))
        .transition(DrawableTransitionOptions.withCrossFade(CROSSFADE_MILLIS))
        .apply {
            if (onReady != null) listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean,
                ): Boolean {
                    onReady()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?, target: Target<Drawable>?,
                    dataSource: DataSource?, isFirstResource: Boolean,
                ): Boolean {
                    onReady()
                    return false
                }
            })
        }
        .into(view)
}

private const val CROSSFADE_MILLIS = 200

fun getListAsString(list: List<Any>): String {
    val sb = StringBuilder()

    list.forEach { sb.append("$it ") }
    return sb.toString().trim()
}