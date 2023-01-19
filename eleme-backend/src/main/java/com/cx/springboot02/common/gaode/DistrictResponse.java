package com.cx.springboot02.common.gaode;

import lombok.Data;

import java.util.List;

@Data
public class DistrictResponse {
    private Integer status;
    private List<District> districts;
}
