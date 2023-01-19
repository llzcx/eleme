package com.cx.springboot02.common.gaode;


import lombok.Data;

import java.util.List;

@Data
public class District {
    private List<String> citycode;
    private String adcode;
    private String name;
    private String center;
    private String level;
    private List<District> districts;
}
