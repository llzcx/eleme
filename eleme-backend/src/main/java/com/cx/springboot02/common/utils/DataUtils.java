package com.cx.springboot02.common.utils;



public class DataUtils {

    public static Double PrecisionControl(Double data,Integer num){
        if(data==null) return null;
        int T = (int)Math.pow(10,num);
        return data = (double)(Math.round(data*T))/T;
    }
}
