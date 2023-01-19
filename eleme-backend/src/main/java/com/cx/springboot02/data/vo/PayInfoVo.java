package com.cx.springboot02.data.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cx.springboot02.pojo.Order;
import lombok.Data;

import java.io.Serializable;

@Data
public class PayInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 支付状态
     */
    private Integer orderState;

    private String createTime;


    private String orderStateStr;


    /**
     * 订单总价格
     */
    private Float totalPrice;
}
