package com.rick.core.di

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun String.toDateFormat(): Date =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(this)!!

@SuppressLint("SimpleDateFormat")
fun Date.toDateString(): String =
    SimpleDateFormat("yyyy-MM-dd").format(this)
