package com.rick.ecatalogs.auth

import com.rick.ecatalogs.authentication.auth_data.AuthSession
import com.rick.ecatalogs.authentication.auth_data.di.AuthModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/** Signed in by default. Never offers One Tap: its system sheet would cover the app. */
class TestAuthSession : AuthSession {
    override var isSignedIn: Boolean = true

    override val offersOneTap: Boolean = false

    var deletedAccount = false
        private set

    override fun signOut() {
        isSignedIn = false
    }

    override fun deleteAccount(onComplete: () -> Unit) {
        deletedAccount = true
        isSignedIn = false
        onComplete()
    }
}

/** Replaces Firebase with [TestAuthSession] in every app instrumented test (one per test). */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [AuthModule::class])
object TestAuthModule {
    @Provides
    @Singleton
    fun providesTestAuthSession(): TestAuthSession = TestAuthSession()

    @Provides
    fun providesAuthSession(session: TestAuthSession): AuthSession = session
}
