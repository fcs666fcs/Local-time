package com.localtime.core
import com.localtime.core.geo.CoordinateParser
import org.junit.Assert.assertEquals
import org.junit.Test
class CoordinateParserTest{
 @Test fun eastPositive(){assertEquals(121.0,CoordinateParser.parseLongitude("121E"),0.0001)}
 @Test fun westNegative(){assertEquals(-73.0,CoordinateParser.parseLongitude("73W"),0.0001)}
 @Test fun southNegative(){assertEquals(-33.0,CoordinateParser.parseLatitude("33S"),0.0001)}
}
