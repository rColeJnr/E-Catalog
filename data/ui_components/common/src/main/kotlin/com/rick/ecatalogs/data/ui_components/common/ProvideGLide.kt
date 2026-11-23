package com.rick.ecatalogs.data.ui_components.common

import android.widget.ImageView
import android.graphics.drawable.ColorDrawable
import com.bumptech.glide.Glide

/**
 * Static placeholder color. An animated CircularProgressDrawable per item forces a redraw on
 * every frame while images load, which kept the RenderThread busy.
 */
private const val PLACEHOLDER_COLOR = 0x1F808080

fun provideGlide(view: ImageView, src: String) {
    Glide.with(view)
        .load(src)
        .placeholder(ColorDrawable(PLACEHOLDER_COLOR))
        .into(view)
}

fun getListAsString(list: List<Any>): String {
    val sb = StringBuilder()

    list.forEach { sb.append("$it ") }
    return sb.toString().trim()
}