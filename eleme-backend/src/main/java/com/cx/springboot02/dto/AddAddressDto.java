package com.cx.springboot02.dto;


import lombok.Data;

@Data
public class AddAddressDto {
    private String name;
    private Long cid;
    private String phone;
    private String geohash;
    private String address;
    private String specificAddress;
    private Boolean isDefault;
    private String tag;
    private Boolean sex;

}
