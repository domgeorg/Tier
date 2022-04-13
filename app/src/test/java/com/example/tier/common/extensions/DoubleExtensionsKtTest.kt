package com.example.tier.common.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class DoubleExtensionsKtTest {

    @Test
    fun `given distance 1000 when formatDistance then should return distance in m`() {
        val expected = "1000m"

        val actual = 1000.0.formatDistance()

        assertEquals(expected, actual)
    }

    @Test
    fun `given distance greater than 1000 when formatDistance then should return distance in km with rounding`() {
        val expected = "2.35km"

        val actual = 2345.0.formatDistance()

        assertEquals(expected, actual)
    }

    @Test
    fun `given distance less than 1000 when formatDistance then should return distance in km with rounding`() {
        val expected = "345m"

        val actual = 345.2.formatDistance()

        assertEquals(expected, actual)
    }
}
