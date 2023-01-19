package com.cx.springboot02.common.order.payState;


import com.cx.springboot02.common.order.Context;
import com.cx.springboot02.common.E.PayState;
import com.cx.springboot02.common.utils.DateUtils;
import com.cx.springboot02.pojo.Order;
import lombok.Data;

@Data
public class UnPaidState implements State {
    @Override
    public Boolean doAction(Context context) {
        context.setState(this);
        Order order = new Order();
        order.setId(order.getId());
        order.setOrderState(getPayState().getCode());
        order.setCreateTime(DateUtils.getCurrentTime());
        Context.orderMapper.updateById(order);
        return true;
    }

    @Override
    public PayState getPayState() {
        return PayState.UNPAID;
    }
}
