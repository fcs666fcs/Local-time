package com.localtime.core

import com.localtime.core.astronomy.SolarCalculator
import com.localtime.core.time.LocalTimeCalculator
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/** Compact deterministic regression coverage replacing thousands of generated test methods. */
class GeneratedSolarRegressionTest {
    @Test fun dailySolarValuesStayFinite() {
        for (year in listOf(2024, 2025, 2026)) {
            for (day in 1..365) {
                val date = LocalDate.ofYearDay(year, day.coerceAtMost(dateLength(year)))
                val dec = SolarCalculator.declinationDeg(date)
                val eot = SolarCalculator.equationOfTimeMinutes(date)
                val pos = SolarCalculator.solarPosition(date, (day * 3L % 1440L).toDouble(), 25.0)
                assertTrue(dec.isFinite())
                assertTrue(eot.isFinite())
                assertTrue(pos.altitudeDeg in -90.0..90.0)
                assertTrue(pos.azimuthDeg in 0.0..360.0)
            }
        }
    }

    @Test fun localMeanTimeWrapsAcrossMidnight() {
        for (lon in -180..180 step 15) {
            val result = LocalTimeCalculator.highSchoolLocalMinutes(1439.0, lon.toDouble(), 0.0)
            assertTrue(result in 0.0..<1440.0)
        }
    }

    private fun dateLength(year: Int): Int = if (java.time.Year.of(year).isLeap) 366 else 365
}
