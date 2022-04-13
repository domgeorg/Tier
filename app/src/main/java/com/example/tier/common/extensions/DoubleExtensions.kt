package com.example.tier.common.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

fun Double.formatDistance(): String {
    if (this > 1000.0) {
        return BigDecimal.valueOf(this / 1000.0)
            .setScale(2, RoundingMode.HALF_UP).let { "${it}km" }
    }
    return "${this.roundToInt()}m"
}
