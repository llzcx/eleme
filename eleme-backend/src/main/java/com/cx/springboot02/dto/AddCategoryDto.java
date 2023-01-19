package com.cx.springboot02.dto;

import lombok.Data;

@Data
public class AddCategoryDto {
    private String name;
    private String description;
    private Long businessId;
}
