package com.cx.springboot02.common.redis.HotKeyCache;


import lombok.Data;

@Data
public class ZSetData {
    private String word;
    private Integer num;
}
