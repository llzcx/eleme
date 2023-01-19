package com.cx.springboot02.data.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cx.springboot02.dto.CartDataDto;
import lombok.Data;

import java.util.List;

@Data
public class ShopLOrderListVo {
    private Long id;

    /**
     * 接收地址的id
     */
    private Long addressId;

    private Long customerId;

    private Long buycartId;

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
     * 创建时间
     */
    private String createTime;


    /**
     * 是否已评论
     */
    private Integer commentState;


    /**
     * 订单到达时间
     */
    private String arrivalTime;


    /**
     * 订单预期送达时间
     */
    private String expectedTime;


    /**
     * 售后状态
     */
    private Integer afterSalesStatus;

    /**
     * 用户的名字
     */
    private String customerName;

    /**
     * 收货地址
     */
    private String address;

    private String phone;

    /**
     * 商店名字
     */
    private String shopName;


    /**
     * 商店位置
     */
    private String shopAddress;


    /**
     * 状态的string格式
     */
    private String stateStr;

    /**
     * 订单总花费
     */
    private Float totalPrice;


    /**
     * 地址用户名
     */
    private String addressUserName;

    private List<CartDataDto> foodDetail;








}
