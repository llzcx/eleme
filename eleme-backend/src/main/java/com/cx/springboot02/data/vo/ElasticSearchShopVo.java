package com.cx.springboot02.data.vo;


import lombok.Data;

@Data
public class ElasticSearchShopVo {
    private Long id;
    private String name;
    private String longitude;
    private String latitude;
}
