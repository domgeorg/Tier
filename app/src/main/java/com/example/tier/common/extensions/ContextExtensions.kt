package com.example.tier.common.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.example.tier.common.definitions.Definitions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlin.math.min

fun Context.bitmapDescriptorFrom(resId: Int): BitmapDescriptor? =
    ContextCompat.getDrawable(this, resId)?.let { drawable ->
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        drawable.draw(Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }

fun Context.mapBound(padding: Double = Definitions.DEFAULT_MAP_BOUND_PADDING): Int =
    resources.displayMetrics.let {
        (min(it.widthPixels, it.heightPixels) * padding).toInt()
    }
