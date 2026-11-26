package com.rick.ecatalogs.authentication.auth_data.di

import com.rick.ecatalogs.authentication.auth_data.AuthSession
import com.rick.ecatalogs.authentication.auth_data.FirebaseAuthSession
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Public (not internal) so app instrumented tests can replace it with @TestInstallIn.
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindsAuthSession(session: FirebaseAuthSession): AuthSession
}
