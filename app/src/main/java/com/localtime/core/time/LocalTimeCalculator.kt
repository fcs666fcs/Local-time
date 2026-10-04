package com.localtime.core.time
import java.time.*
import kotlin.math.*
object LocalTimeCalculator{
 const val MINUTES_PER_DAY=1440.0
 fun centralLongitude(offsetHours:Double)=offsetHours*15.0
 fun longitudeDeltaMinutes(longitude:Double,offsetHours:Double)=(longitude-centralLongitude(offsetHours))*4.0
 fun highSchoolLocalMinutes(civilMinutes:Double,longitude:Double,offsetHours:Double)=floorMod(civilMinutes+longitudeDeltaMinutes(longitude,offsetHours),MINUTES_PER_DAY)
 fun meanSolarMinutes(utcMinutes:Double,longitude:Double)=floorMod(utcMinutes+longitude*4.0,MINUTES_PER_DAY)
 fun floorMod(value:Double,modulus:Double):Double{var x=value%modulus;if(x<0)x+=modulus;return x}
 fun minutesOfDay(time:LocalTime)=time.hour*60.0+time.minute+time.second/60.0+time.nano/60_000_000_000.0
 fun formatMinutes(minutes:Double):String{val total=round(floorMod(minutes,MINUTES_PER_DAY)).toInt();return "%02d:%02d".format((total/60)%24,total%60)}
 fun zoneOffsetHours(zdt:ZonedDateTime)=zdt.offset.totalSeconds/3600.0
}
