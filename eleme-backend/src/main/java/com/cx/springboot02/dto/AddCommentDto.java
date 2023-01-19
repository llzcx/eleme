package com.cx.springboot02.dto;


import com.cx.springboot02.pojo.Customer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AddCommentDto implements Serializable {
    //图片列表
    private String[] imgList;

    //客户id
    private Long  customerId;

    private Long orderId;

    //内容
    private String content;

    private Long parent;

    private Long topParent;

    private Float rating;


    private Boolean isCustomer;



}
