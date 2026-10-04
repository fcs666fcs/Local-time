package com.localtime.core.model
import java.time.*
data class GeoCoordinate(val latitudeDeg: Double,val longitudeDeg: Double,val accuracyMeters: Double?=null){init{require(latitudeDeg in -90.0..90.0);require(longitudeDeg in -180.0..180.0)}}
data class TimeSnapshot(val instant:Instant,val zoneId:ZoneId,val civilTime:ZonedDateTime,val localMeanMinutes:Double,val localApparentMinutes:Double?)
data class SolarPosition(val altitudeDeg:Double,val azimuthDeg:Double,val declinationDeg:Double,val hourAngleDeg:Double)
enum class DaylightState{NORMAL,POLAR_DAY,POLAR_NIGHT}
data class SunriseSunset(val state:DaylightState,val sunriseMinutes:Double?,val sunsetMinutes:Double?,val daylightMinutes:Double?)
data class SolarSummary(val position:SolarPosition,val sunriseSunset:SunriseSunset,val subsolarLatitude:Double,val subsolarLongitude:Double)
data class PlaceState(val coordinate:GeoCoordinate,val source:LocationSource,val name:String="当前位置")
enum class LocationSource{GPS,MANUAL,LAST_KNOWN}
