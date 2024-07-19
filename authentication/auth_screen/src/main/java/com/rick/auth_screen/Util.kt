package com.rick.auth_screen

import android.util.Patterns

fun isValidEmail(email: String): Boolean {
    return if (email.isNotBlank())
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    else false
}

fun isValidPassword(password: String): Boolean {
    return if (password.isNotEmpty())
        password.matches(PASSWORD_REGEX)
    else
        false
}

fun isValidUsername(username: String): Boolean {
    return if (username.isNotBlank())
        username.matches(USERNAME_REGEX)
    else
        false
}

// No white spaces, At least # characters.
private val PASSWORD_REGEX = "^(?=\\S+$).{6,}$".toRegex()
private val USERNAME_REGEX = "^(?=\\S+$).{3,12}$".toRegex()