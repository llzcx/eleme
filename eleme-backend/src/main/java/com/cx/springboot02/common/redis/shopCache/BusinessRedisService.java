package com.cx.springboot02.common.redis.shopCache;


import com.cx.springboot02.common.redis.RedisOperator;
import com.cx.springboot02.pojo.Business;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.cx.springboot02.common.redis.RedisFinal.ELEME;
import static com.cx.springboot02.common.redis.RedisFinal.SHOP;

@Component
public class BusinessRedisService {

    private final String prefix = ELEME+SHOP;

    @Autowired
    RedisOperator redisOperator;
    
    public void saveShop(Business business){
        redisOperator.setObject(prefix+business.getId(),business);
    }

    public void deleteShopById(Long shopId){
        redisOperator.del(prefix+shopId);
    }

    public void deleteAllShop(){
        Set<String> keys = redisOperator.keys(prefix);
        for (String key : keys) {
            redisOperator.del(key);
        }
    }

    public Business getShopById(Long id){
        Business business = redisOperator.getObject(prefix + id,Business.class);
        return business;
    }
}
