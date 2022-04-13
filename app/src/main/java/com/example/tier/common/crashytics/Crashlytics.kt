package com.example.tier.common.crashytics

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class Crashlytics @Inject constructor(private val firebaseCrashlytics: FirebaseCrashlytics) {

    init {
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true)
    }

    fun log(tag: String, message: String) {
        Log.d(tag, message)
        firebaseCrashlytics.log("$tag: $message")
    }

    fun log(throwable: Throwable) {
        Log.e("Crashlytics::recordException", throwable.message ?: "")
        firebaseCrashlytics.recordException(throwable)
    }
}
