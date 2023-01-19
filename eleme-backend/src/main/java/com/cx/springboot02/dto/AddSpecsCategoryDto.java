package com.cx.springboot02.dto;

import lombok.Data;

@Data
public class AddSpecsCategoryDto {
    private Long goodId;
    private String name;
    private String specsName;
    private String specsDescription;
}
