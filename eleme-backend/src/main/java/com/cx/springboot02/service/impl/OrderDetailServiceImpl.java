package com.cx.springboot02.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.mapper.Order_detailsMapper;
import com.cx.springboot02.pojo.OrderDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class OrderDetailServiceImpl extends ServiceImpl<Order_detailsMapper, OrderDetails> {



    @Autowired
    private Order_detailsMapper order_detailsMapper;

    /**
     * 获取一个订单的详细信息
     * @param oid
     * @return
     */
    public List<OrderDetails> getOrderDetailByOId(Long oid){
        Map<String ,Object> mp = new HashMap<>();
        mp.put("order_id",oid);
        return order_detailsMapper.selectByMap(mp);
    }
}
