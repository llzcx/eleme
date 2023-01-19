package com.cx.springboot02.data.vo;


import lombok.Data;

@Data
public class SkuVo {
    private Long shopId;
    private Long categoryId;
    private Long spuId;
    private Long skuId;
    private String name;
    private Float price;
    private String specsList;
    private String specsListStr;
    private Float packingFee;
}
