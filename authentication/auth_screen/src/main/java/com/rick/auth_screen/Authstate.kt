package com.rick.auth_screen

data class AuthScreenState(
    val authScreenState: Boolean = true,
    val progressState: Boolean = false,
    val authErrorMsg: String = ""
)

data class InputState(
    val email: String = "",
    val isValidEmail: Boolean = false,
    val username: String = "",
    val isValidUsername: Boolean = false,
    val showUsernameTips: Boolean = false,
)

data class PasswordState(
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val showPasswordTips: Boolean = false,
    val showConfirmPassword: Boolean = false,
)