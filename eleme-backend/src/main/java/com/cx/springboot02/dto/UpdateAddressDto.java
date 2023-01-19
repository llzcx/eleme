package com.cx.springboot02.dto;


import lombok.Data;

@Data
public class UpdateAddressDto {
    private Long id;
    private String name;
    private Long cid;
    private String phone;
    private String geohash;
    private String specificAddress;
    private Boolean isDefault;
    private String tag;
    private Boolean sex;
}
