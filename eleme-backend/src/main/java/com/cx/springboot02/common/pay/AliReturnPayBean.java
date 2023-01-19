package com.cx.springboot02.common.pay;

import lombok.Data;

import java.io.Serializable;


@Data
public class AliReturnPayBean  implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 开发者的app_id
     */
    private String app_id;

    /**
     * 商户订单号
     */
    private String out_trade_no;

    /**
     * 签名
     */
    private String sign;

    /**
     * 交易状态
     */
    private String trade_status;

    /**
     *  支付宝交易号
     */
    private String trade_no;

    /**
     * 交易的金额
     */
    private String total_amount;

    private String product_code = "FAST_INSTANT_TRADE_PAY";

    private String timeout_express = "10m";



}