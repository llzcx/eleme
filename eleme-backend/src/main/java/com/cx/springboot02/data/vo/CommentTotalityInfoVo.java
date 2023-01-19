package com.cx.springboot02.data.vo;


import lombok.Data;

@Data
public class CommentTotalityInfoVo {
    //综合评价
    private Float totalRating;
    //高于周围商家
    private Float AboveAmbient;
    //服务态度
    private Float ServiceAttitudeScore;
    //食物得分
    private Float FoodScore;
    //送达时间
    private Integer arriveTime;
    //各个评论分类的数量
    private CommentCategory commentCategory;
}
