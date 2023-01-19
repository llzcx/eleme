package com.cx.springboot02.dto;


import lombok.Data;

@Data
public class BCMessageDto {
    /**
     * 身份
     */
    private String identity;


    /**
     * 商家id
     */
    private String businessId;


    /**
     * 用户的id
     */
    private String customerId;

    /**
     * 发送的内容
     */
    private String content;
}
