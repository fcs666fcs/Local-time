package com.localtime.core.geo
import com.localtime.core.model.GeoCoordinate
object CoordinateParser{
 fun parseLatitude(t:String)=parse(t,90.0);fun parseLongitude(t:String)=parse(t,180.0)
 fun parse(t:String,max:Double):Double{val u=t.trim().uppercase();val raw=u.replace("°","").replace("E","").replace("W","").replace("N","").replace("S","");val v=raw.toDoubleOrNull()?:throw IllegalArgumentException("无效经纬度");val s=if(u.contains('W')||u.contains('S'))-1 else 1;val r=v*s;require(r in -max..max);return r}
 fun validate(lat:Double,lon:Double)=GeoCoordinate(lat,lon)
}
