package com.rick.screen_movie.util

import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun provideGlide(view: ImageView, src: String) {
    val glide = Glide.with(view)
    val circularProgressDrawable = CircularProgressDrawable(view.context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }
    val options = RequestOptions().placeholder(circularProgressDrawable)
    glide
        .load(src)
        .apply(options)
        .into(view)
}