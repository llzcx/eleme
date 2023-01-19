package com.cx.springboot02.common.order.payState;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cx.springboot02.common.order.Context;
import com.cx.springboot02.common.E.PayState;
import com.cx.springboot02.common.utils.DateUtils;
import com.cx.springboot02.pojo.Order;
import lombok.Data;

/**
 * 派送
 */
@Data
public class DispatchState implements State {
    @Override
    public Boolean doAction(Context context) {
        context.setState(this);
        Order order = new Order();
        order.setOrderState(getPayState().getCode());
        order.setDispatchTime(DateUtils.getCurrentTime());
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_state",PayState.MAKING.getCode());
        queryWrapper.eq("id",context.getOrder().getId());
        int update = Context.orderMapper.update(order, queryWrapper);
        return update > 0;
    }

    @Override
    public PayState getPayState() {
        return PayState.DISPATCH;
    }
}
