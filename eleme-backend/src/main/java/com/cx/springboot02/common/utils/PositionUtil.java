package com.cx.springboot02.common.utils;

public class PositionUtil {
    public static String ToGeohash(Double lon, Double lat){
        if(lon==null || lat==null) return null;
        return lon + "," +lat;
    }
    public static String ToGeohash(String lon, String lat){
        if(lon==null || lat==null) return null;
        return lon + "," +lat;
    }
    public static String ToGeohash(Integer lon, Integer lat){
        if(lon==null || lat==null) return null;
        return lon + "," +lat;
    }
}
