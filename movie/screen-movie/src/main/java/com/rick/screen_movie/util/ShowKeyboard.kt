package com.rick.screen_movie.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun showSoftKeyboard(view: View, context: Context) {
    if (view.requestFocus()) {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}
