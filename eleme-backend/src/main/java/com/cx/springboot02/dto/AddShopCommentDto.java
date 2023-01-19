package com.cx.springboot02.dto;


import lombok.Data;

@Data
public class AddShopCommentDto {

    //客户id
    private Long  customerId;

    //订单的id
    private Long orderId;

    //内容
    private String content;

    //被回复的评论id
    private String BeReplyId;


    private Boolean isCustomer;

}
