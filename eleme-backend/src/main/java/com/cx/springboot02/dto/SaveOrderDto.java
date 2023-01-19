package com.cx.springboot02.dto;

import lombok.Data;

import java.util.List;

@Data
public class SaveOrderDto {

    /**
     * 收获地址id
     */
    private Long addressId;

    /**
     * 用户id
     */
    private Long customerId;


    /**
     * 商店id
     */
    private Long shopId;

    /**
     * 用户备注
     */
    private String remarks;

    private List<BuyCartDataDto> list;


}
