package com.cx.springboot02.data.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentCategory implements Serializable {
    //所有
    private Integer all = 0;
    //满意
    private Integer satisfied = 0;
    //不满意
    private Integer pictorial = 0;
    //味道好
    private Integer tastesGood = 0;
    //送货快
    private Integer FastDelivery = 0;
    //分量足
    private Integer portions = 0;
    //包装精美
    private Integer ExquisitePackage = 0;
    //干净卫生
    private Integer CleanAndSanitary = 0;
    //食材新鲜
    private Integer FreshIngredients = 0;
    //服务不错
    private Integer goodService = 0;
}
