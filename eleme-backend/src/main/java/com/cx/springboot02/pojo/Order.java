package com.cx.springboot02.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 陈翔
 * @since 2022-10-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("`Order`")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 商铺地址
     */
    private String shopAddress;

    private String shopGeohash;
    /**
     * 电话
     */
    private String phone;


    private Integer floatDeliveryFee;

    private Float totalPrice;

    private Float realPay;

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
