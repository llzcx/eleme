package com.cx.springboot02.data.vo;


import com.cx.springboot02.pojo.Comment;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo {

    private Long id;

    private Long customerId;

    private Long orderId;
    //顾客名字
    private String customerName;
    //顾客头像
    private String customerImg;
    //内容
    private String content;
    //聊天发表时间
    private String createTime;
    //评分
    private Float rating;

    private List<String> images;

    /**
     * 是否已经回复
     */
    private Boolean haveReplay;

    private String haveReplayStr;

    private Comment childComment;

}
