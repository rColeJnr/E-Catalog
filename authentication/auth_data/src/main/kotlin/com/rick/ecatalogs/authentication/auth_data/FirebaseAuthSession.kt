package com.rick.ecatalogs.authentication.auth_data

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

/** [AuthSession] backed by Firebase Auth. */
class FirebaseAuthSession @Inject constructor() : AuthSession {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    override val isSignedIn: Boolean get() = auth.currentUser != null

    override val offersOneTap: Boolean = true

    override fun signOut() = auth.signOut()

    override fun deleteAccount(onComplete: () -> Unit) {
        val user = auth.currentUser ?: return onComplete()
        user.delete().addOnCompleteListener { onComplete() }
    }
}
