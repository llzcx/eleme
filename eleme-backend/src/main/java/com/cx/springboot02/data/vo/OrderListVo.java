package com.cx.springboot02.data.vo;

import com.cx.springboot02.pojo.OrderDetails;
import lombok.Data;

import java.util.List;

@Data
public class OrderListVo {
    private static final long serialVersionUID = 1L;
    private Long id;


    private Long customerId;



    private Long shopId;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 坐标
     */
    private String geohash;


    /**
     * 支付状态
     */
    private Integer orderState;

    /**
     * 支付状态的字符串形式
     */
    private String orderStateStr;


    /**
     * 是否已评论
     */
    private Boolean commentState;

    /**
     * 订单预期送达时间
     */
    private String expectedTime;


    /**
     * 售后状态
     */
    private Integer afterSalesStatus;

    /**
     * 收货人
     */
    private String consignee;



    /**
     * 地址
     */
    private String addressName;


    /**
     * 电话
     */
    private String phone;

    private List<OrderDetails> orderDetailsList;

    private Float totalPrice;


    private String shopImage;


    private String shopName;

    private String shopGeohash;

    private String shopAddress;

    private Integer floatDeliveryFee;

    /**
     * 创建时间
     */
    private String createTime;

    private String payTime;

    private String receivingTime;

    private String dispatchTime;

    private String finishTime;

    private String arrivalTime;

    private String cancelTime;

    private String timeoutTime;
}
