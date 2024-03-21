package com.rick.auth_screen

import android.util.Patterns

fun isValidEmail(email: String): Boolean {
    if (email.isNotBlank()) return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return false
}

fun isValidPassword(password: String): Boolean {
    if (password.isNotBlank()) return password.matches(PASSWORD_REGEX)
    return false
}


fun isValidUsername(username: String): Boolean {
    if (username.isNotBlank()) return username.matches(USERNAME_REGEX)
    return false
}

// No white spaces, At least # characters.
private val PASSWORD_REGEX = "^(?=\\S+$).{6,}$".toRegex()
private val USERNAME_REGEX = "^(?=\\S+$).{3,12}$".toRegex()