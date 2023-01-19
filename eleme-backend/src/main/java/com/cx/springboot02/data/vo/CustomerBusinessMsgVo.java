package com.cx.springboot02.data.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class CustomerBusinessMsgVo {

    private Long id;

    /**
     * 顾客id
     */
    private Long customerId;

    /**
     * 商家id
     */
    private Long businessId;

    /**
     * 内容
     */
    private String content;

    /**
     * 发送者是否为顾客
     */
    private Boolean customerSender;

    /**
     * 是否被删除
     */
    private Boolean isDelete;


    private String createTime;

    /**
     * 消息发送者的头像地址
     */
    private String imagePath;



}
