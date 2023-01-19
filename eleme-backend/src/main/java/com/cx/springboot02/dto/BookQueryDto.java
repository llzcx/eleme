package com.cx.springboot02.dto;

import lombok.Data;


@Data
public class BookQueryDto {
    String name;
    Integer[] price;
    Integer size;
    Integer page;

    public Integer offset(){
        return (page-1)*size;
    }
    public Integer limit(){
        return size;
    }
}


