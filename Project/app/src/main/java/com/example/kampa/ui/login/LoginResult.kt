package com.example.kampa.ui.login

/**
 * Authentication result
 */
data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)