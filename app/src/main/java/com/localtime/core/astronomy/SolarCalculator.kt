package com.localtime.core.astronomy
import com.localtime.core.model.*
import com.localtime.core.time.LocalTimeCalculator
import java.time.*
import kotlin.math.*
object SolarCalculator{
 private const val DEG=PI/180.0;private const val RAD=180.0/PI;private const val SUNRISE_ALTITUDE=-0.833
 fun declinationDeg(date:LocalDate):Double=23.44*sin(DEG*(360.0/365.0*(date.dayOfYear-81)))
 fun equationOfTimeMinutes(date:LocalDate):Double{val b=DEG*(360.0/364.0*(date.dayOfYear-81));return 9.87*sin(2*b)-7.53*cos(b)-1.5*sin(b)}
 fun solarPosition(date:LocalDate,localMeanMinutes:Double,latitudeDeg:Double):SolarPosition{val d0=declinationDeg(date);val apparent=localMeanMinutes+equationOfTimeMinutes(date);val ha=15.0*(apparent/60.0-12.0);val p=latitudeDeg*DEG;val d=d0*DEG;val h=ha*DEG;val alt=asin((sin(p)*sin(d)+cos(p)*cos(d)*cos(h)).coerceIn(-1.0,1.0))*RAD;val az=((atan2(sin(h),cos(h)*sin(p)-tan(d)*cos(p))*RAD+180)%360+360)%360;return SolarPosition(alt,az,d0,ha)}
 fun sunriseSunset(date:LocalDate,latitudeDeg:Double):SunriseSunset{val p=latitudeDeg*DEG;val d=declinationDeg(date)*DEG;val h0=SUNRISE_ALTITUDE*DEG;val den=cos(p)*cos(d);if(abs(den)<1e-12)return polar(p,d);val c=(sin(h0)-sin(p)*sin(d))/den;if(c>1)return SunriseSunset(DaylightState.POLAR_NIGHT,null,null,null);if(c< -1)return SunriseSunset(DaylightState.POLAR_DAY,null,null,1440.0);val h=acos(c.coerceIn(-1.0,1.0))*RAD;val day=2*h/15*60;return SunriseSunset(DaylightState.NORMAL,720-day/2,720+day/2,day)}
 private fun polar(p:Double,d:Double)=if(p*d>=0)SunriseSunset(DaylightState.POLAR_DAY,null,null,1440.0)else SunriseSunset(DaylightState.POLAR_NIGHT,null,null,0.0)
 fun summary(date:LocalDate,localMeanMinutes:Double,latitudeDeg:Double)=SolarSummary(solarPosition(date,localMeanMinutes,latitudeDeg),sunriseSunset(date,latitudeDeg),declinationDeg(date),((12-localMeanMinutes/60)*15+540)%360-180)
 fun trajectory(date:LocalDate,latitudeDeg:Double,samples:Int=97)=List(samples){i->solarPosition(date,i*1440.0/(samples-1),latitudeDeg)}
 fun coordinateTimeSnapshot(instant:Instant,zone:ZoneId,coordinate:GeoCoordinate):TimeSnapshot{val civil=instant.atZone(zone);val utc=instant.atZone(ZoneOffset.UTC);val mean=LocalTimeCalculator.meanSolarMinutes(LocalTimeCalculator.minutesOfDay(utc.toLocalTime()),coordinate.longitudeDeg);return TimeSnapshot(instant,zone,civil,mean,LocalTimeCalculator.floorMod(mean+equationOfTimeMinutes(civil.toLocalDate()),1440.0))}
}
