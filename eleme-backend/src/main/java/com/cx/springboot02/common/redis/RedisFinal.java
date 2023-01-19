package com.cx.springboot02.common.redis;

public interface RedisFinal {
    //系统在redis中的前缀
    String ELEME = "ELEME:";

    //业务
    String SHOP = "SHOP:";
    String ORDER = "ORDER:";
    String HOT_KEY = "HOT_KEY:";
    String CITY_LIST = "CITYLIST";



    static String Un(String... strs){
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : strs) {
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }
}
