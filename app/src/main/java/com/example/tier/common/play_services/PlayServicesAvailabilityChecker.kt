package com.example.tier.common.play_services

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlayServicesAvailabilityChecker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val googleApiAvailability: GoogleApiAvailability
) {
    fun isGooglePlayServicesAvailable(available: () -> Unit, unavailable: () -> Unit) =
        when (googleApiAvailability.isGooglePlayServicesAvailable(context)) {
            ConnectionResult.SUCCESS -> available.invoke()
            else -> unavailable.invoke()
        }
}
