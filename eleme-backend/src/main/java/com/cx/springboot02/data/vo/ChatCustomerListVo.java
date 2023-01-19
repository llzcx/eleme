package com.cx.springboot02.data.vo;


import lombok.Data;

@Data
public class ChatCustomerListVo {
    private Long id;
    /**
     * 昵称
     */
    private String name;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 商家是否已读
     */
    private Boolean businessRead;


    /**
     * 用户是否已读
     */
    private Boolean customerRead;



}
