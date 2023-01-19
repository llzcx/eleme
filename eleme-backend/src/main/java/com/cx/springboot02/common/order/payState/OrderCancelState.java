package com.cx.springboot02.common.order.payState;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cx.springboot02.common.order.Context;
import com.cx.springboot02.common.E.PayState;
import com.cx.springboot02.common.utils.DateUtils;
import com.cx.springboot02.pojo.Order;



public class OrderCancelState implements State{
    @Override
    public Boolean doAction(Context context) {
        context.setState(this);
        Order order = new Order();
        order.setOrderState(getPayState().getCode());
        order.setCancelTime(DateUtils.getCurrentTime());
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_state",PayState.PAID.getCode());
        queryWrapper.eq("id",context.getOrder().getId());
        int update = Context.orderMapper.update(order, queryWrapper);
        return update > 0;
    }

    @Override
    public PayState getPayState() {
        return PayState.ORDER_CANCEL;

    }
}
