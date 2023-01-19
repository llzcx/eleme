package com.cx.springboot02.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cx.springboot02.pojo.Manager;
import com.cx.springboot02.pojo.OrderDetails;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Order_detailsMapper extends BaseMapper<OrderDetails> {

}
