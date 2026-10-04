package com.localtime.core
import com.localtime.core.astronomy.SolarCalculator
import com.localtime.core.model.DaylightState
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
class SolarCalculatorTest{
 @Test fun declinationWithinEarthRange(){for(n in 1..365){val d=SolarCalculator.declinationDeg(LocalDate.ofYearDay(2025,n));assertTrue(d in -24.0..24.0)}}
 @Test fun eotIsFinite(){for(n in 1..365){assertTrue(SolarCalculator.equationOfTimeMinutes(LocalDate.ofYearDay(2025,n)).isFinite())}}
 @Test fun equatorEquinoxNoonIsHigh(){val p=SolarCalculator.solarPosition(LocalDate.of(2025,3,20),720.0,0.0);assertTrue(p.altitudeDeg>85)}
 @Test fun polarDayDoesNotInventSunset(){val s=SolarCalculator.sunriseSunset(LocalDate.of(2025,6,21),80.0);assertEquals(DaylightState.POLAR_DAY,s.state);assertNull(s.sunriseMinutes);assertNull(s.sunsetMinutes)}
 @Test fun trajectoryHasRequestedSamples(){assertEquals(97,SolarCalculator.trajectory(LocalDate.now(),25.0,97).size)}
}
