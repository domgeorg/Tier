package com.example.tier.di

import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApplicationModule {

    @Singleton
    @Provides
    fun provideGoogleApiAvailability() = GoogleApiAvailability.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseCrashlytics() = FirebaseCrashlytics.getInstance()
}
