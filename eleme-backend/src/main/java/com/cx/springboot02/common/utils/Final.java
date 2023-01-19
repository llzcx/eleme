package com.cx.springboot02.common.utils;

public interface Final {
    String CUSTOM_TOKEN = "custom_token:";
    String BUSINESS_TOKEN = "business_token:";
    String MANAGE_TOKEN = "manage_token:";
    String CUSTOM_SERVICE_TOKEN = "customerService_token:";


    String RETURN_REASON01 = "订单状态异常,已经为您退款,预计三个工作日内返还到您的账户中";
    String RELEASE_SUCCESS ="RELEASE_SUCCESS";

    String PREFIX_SHOP_ID = "shop-";
    String PREFIX_SPU_ID = "spu-";


    long USERINFO_LIVE_TIME = 1800L;
    long LOCK_TIME = 5000L;//毫秒


    String REDIS_ELEME = "ELEME:";
    String REDIS_SAVE_LOGIN_INFO = "login:";

    static String Un(String... strs){
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : strs) {
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }
}
