package com.cx.springboot02.data.vo;

import lombok.Data;

@Data
public class OrderDetailsVo {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long orderId;
    private String goodsName;
    private String imagePath;
    private Integer num;
    private Float price;
    private Float packingFee;
    private String specsList;
    private Float totalPrice;
    private Integer floatDeliveryFee;
    private Float realPay;
}
