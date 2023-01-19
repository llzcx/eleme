package com.cx.springboot02.pojo;


import lombok.Data;

import java.io.Serializable;

/**
 * 订单详情页需要的
 */
@Data
public class OrderDetails implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long orderId;
    private String goodsName;
    private String imagePath;
    private Integer num;
    private Float price;
    private Float packingFee;
    private String specsList;
    private Long skuId;
}
