package com.example.tier.common.location

import com.google.android.gms.common.api.ResolvableApiException

sealed class SettingsResult {
    object Satisfied : SettingsResult()
    class Resolvable(val exception: ResolvableApiException) : SettingsResult()
    class NotResolvable(val exception: Exception) : SettingsResult()
}
