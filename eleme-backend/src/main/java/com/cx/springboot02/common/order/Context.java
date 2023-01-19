package com.cx.springboot02.common.order;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cx.springboot02.SSMPApplication;
import com.cx.springboot02.common.E.PayState;
import com.cx.springboot02.common.order.payState.State;
import com.cx.springboot02.mapper.OrderMapper;
import com.cx.springboot02.pojo.Order;
import com.cx.springboot02.service.impl.OrderServiceImpl;
import com.cx.springboot02.service.impl.PayServiceImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Data
public class Context {
   public static OrderMapper orderMapper;
   public static OrderServiceImpl orderService;
   public static PayServiceImpl payService;
   //手动注入bean
   static {
      orderMapper = SSMPApplication.getBean(OrderMapper.class);
      orderService = SSMPApplication.getBean(OrderServiceImpl.class);
      payService = SSMPApplication.getBean(PayServiceImpl.class);
   }

   //订单id
   private Order order;

   //订单状态类
   private State state;

   /**
    *
    * @param orderId 订单id
    */
   public Context(Long orderId){
      Order order = orderMapper.selectById(orderId);
      //主动抛出异常
      if(order==null) throw new NullPointerException();
      this.setOrder(order);
      //通过code编码获取对应实例
      this.setState(PayState.getStateCls(order.getOrderState()));
   }
}