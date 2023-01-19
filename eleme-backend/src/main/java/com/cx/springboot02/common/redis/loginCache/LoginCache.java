package com.cx.springboot02.common.redis.loginCache;


import com.cx.springboot02.common.redis.RedisFinal;
import com.cx.springboot02.common.redis.RedisOperator;
import com.cx.springboot02.common.utils.Final;
import com.cx.springboot02.pojo.Business;
import com.cx.springboot02.pojo.Customer;
import com.cx.springboot02.pojo.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.MediaName;
import java.util.concurrent.ConcurrentHashMap;

import static com.cx.springboot02.common.utils.Final.*;

@Component
public class LoginCache {

    private final String prefix = Final.REDIS_ELEME +REDIS_SAVE_LOGIN_INFO;

    @Autowired
    RedisOperator redisOperator;

    private ConcurrentHashMap<String,String> loginInfoMap = new ConcurrentHashMap<>();


    public void saveCustomerToken(String token, Customer customer){
        //存入redis
        String redisKey = Un(prefix,CUSTOM_TOKEN,token);
        redisOperator.setObject(redisKey, customer,USERINFO_LIVE_TIME);
        loginInfoMap.put(CUSTOM_TOKEN+customer.getId(),token);
    }

    public void deleteCustomer(Long customerId){
        String token = loginInfoMap.get(CUSTOM_TOKEN+customerId);
        String redisKey = Un(prefix, CUSTOM_TOKEN,token);
        redisOperator.del(redisKey);
    }

    public void saveBusinessToken(String token, Business business){
        //存入redis
        String redisKey = Un(prefix,BUSINESS_TOKEN,token);
        redisOperator.setObject(redisKey, business,USERINFO_LIVE_TIME);
        loginInfoMap.put(BUSINESS_TOKEN+business.getId(),token);
    }

    public void deleteBusiness(Long businessId){
        String token = loginInfoMap.get(BUSINESS_TOKEN+businessId);
        String redisKey = Un(prefix,BUSINESS_TOKEN,token);
        redisOperator.del(redisKey);
    }

    public void saveManageToken(String token, Manager manager){
        //存入redis
        String redisKey = Un(prefix, MANAGE_TOKEN,token);
        redisOperator.setObject(redisKey, manager,USERINFO_LIVE_TIME);
        loginInfoMap.put(MANAGE_TOKEN+manager.getId(),token);
    }

    public void deleteManage(Long businessId){
        String token = loginInfoMap.get(MANAGE_TOKEN+businessId);
        String redisKey = Un(prefix,MANAGE_TOKEN,token);
        redisOperator.del(redisKey);
    }
}
