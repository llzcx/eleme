package com.cx.springboot02.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Address {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 顾客id
     */
    private Long cid;

    /**
     * 姓名 接收人
     */
    private String name;


    /**
     * 电话
     */
    private String phone;


    private String address;

    /**
     * 详细地址
     */
    private String specificAddress;

    /**
     *
     */
    private Boolean sex;


    /**
     *
     */
    private String createTime;

    /**
     * 标签 学校/家
     */
    private String tag;


    /**
     * 是否被啥拿出
     */
    private Boolean isDelete;

    private String geohash;

    private Boolean isDefault;

}
