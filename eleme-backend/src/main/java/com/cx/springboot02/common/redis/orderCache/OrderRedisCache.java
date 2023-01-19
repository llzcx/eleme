package com.cx.springboot02.common.redis.orderCache;


import com.cx.springboot02.common.redis.RedisOperator;
import com.cx.springboot02.pojo.Business;
import com.cx.springboot02.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.cx.springboot02.common.redis.RedisFinal.ELEME;
import static com.cx.springboot02.common.redis.RedisFinal.SHOP;

@Component
public class OrderRedisCache {

    private final String prefix = ELEME+SHOP;

    @Autowired
    RedisOperator redisOperator;



    public void saveOrder(Order order){
        redisOperator.setObject(prefix+order.getId(),order);
    }

    public void deleteOrderById(Long orderId){
        redisOperator.del(prefix+orderId);
    }

    public void deleteAllShop(){
        Set<String> keys = redisOperator.keys(prefix);
        for (String key : keys) {
            redisOperator.del(key);
        }
    }

    public Order getOrderById(Long id){
        Order order = redisOperator.getObject(prefix + id,Order.class);
        return order;
    }
}
