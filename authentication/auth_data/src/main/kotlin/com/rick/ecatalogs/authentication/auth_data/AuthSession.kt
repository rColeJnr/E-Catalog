package com.rick.ecatalogs.authentication.auth_data

/**
 * The signed-in state the app shell needs: whether the login screen can be skipped, and how to
 * leave the account. Injected through Hilt so instrumented tests can replace it
 * (`@TestInstallIn(replaces = [AuthModule::class])`) instead of needing a real Firebase user.
 *
 * Signing in (password, Google, One Tap) still talks to Firebase directly in the login screen.
 */
interface AuthSession {
    /** True when a user is signed in, so the login screen goes straight on to the home screen. */
    val isSignedIn: Boolean

    /**
     * Whether the login screen offers Google One Tap as soon as it opens signed out. Tests turn
     * it off: it opens a system sheet over the app.
     */
    val offersOneTap: Boolean

    fun signOut()

    /** Deletes the signed-in account, then calls [onComplete] (straight away when signed out). */
    fun deleteAccount(onComplete: () -> Unit)
}
