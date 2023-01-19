package com.cx.springboot02.dto;

import lombok.Data;

@Data
public class UpdateSpuNotSingleDto {
    private Long goodsId;

    private String name;

    private String description;

    private Long categoryId;

    private String imagePath;
}
