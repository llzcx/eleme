package com.cx.springboot02.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


@Data
@TableName("`Comment`")
public class Comment  implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long customerId;

    private Long shopId;

    private Long orderId;

    private String content;

    private Boolean isDelete;

    private Long parent;

    private Long topParent;

    private String createTime;

    private Float rating;

    private Boolean isCustomer;
}
