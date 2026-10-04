package com.localtime.core
import com.localtime.core.time.LocalTimeCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
class LocalTimeCalculatorTest{
 @Test fun centralMeridian(){assertEquals(120.0,LocalTimeCalculator.centralLongitude(8.0),0.0001)}
 @Test fun eastOneDegreeAddsFourMinutes(){assertEquals(904.0,LocalTimeCalculator.highSchoolLocalMinutes(900.0,121.0,8.0),0.0001)}
 @Test fun westOneDegreeSubtractsFourMinutes(){assertEquals(896.0,LocalTimeCalculator.highSchoolLocalMinutes(900.0,119.0,8.0),0.0001)}
 @Test fun crossMidnight(){assertEquals(0.0,LocalTimeCalculator.highSchoolLocalMinutes(1439.0,120.25,8.0),0.0001)}
 @Test fun negativeLongitude(){assertEquals(720.0,LocalTimeCalculator.meanSolarMinutes(0.0,180.0),0.0001)}
 @Test fun nepaliCentralMeridian(){assertEquals(86.25,LocalTimeCalculator.centralLongitude(5.75),0.0001)}
}
