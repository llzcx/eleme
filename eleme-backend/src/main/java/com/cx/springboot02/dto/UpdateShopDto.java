package com.cx.springboot02.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UpdateShopDto {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商家名字
     */
    private String name;

    /**
     * 餐馆地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 店铺图片地址
     */
    private String imagePath;

    /**
     * 运费
     */
    private Integer floatDeliveryFee;

    /**
     * 起送价
     */
    private Integer floatMinimumOrderAmount;

    /**
     * 餐馆介绍
     */
    private String description;

    /**
     * 店铺标语
     */
    private String promotionInfo;

    /**
     * 营业时间
     */
    private String startTime;

    /**
     * 打烊时间
     */
    private String endTime;

    /**
     * 商家分类id 比如奶茶 餐馆 炸鸡
     */
    private Long shopClassId;
}
