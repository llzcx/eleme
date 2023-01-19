package com.cx.springboot02.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cx.springboot02.data.vo.OrderListVo;
import com.cx.springboot02.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 陈翔
 * @since 2022-10-05
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    void updateOrder(Order order);

    /**
     * 获取客户端的订单列表
     * @param offset
     * @param pagesize
     * @param cid
     * @param key
     * @param oid
     * @param shopId
     * @return
     */
    List<OrderListVo> selectOrderListByCId(@Param("offset")Integer offset, @Param("pagesize")Integer pagesize, @Param("cid")Long cid, @Param("key")String key, @Param("oid")Long oid, @Param("shopId") Long shopId);


    /**
     * 获取商家后台端的订单列表
     * @param offset
     * @param pagesize
     * @param cid
     * @param key
     * @param oid
     * @param shopId
     * @return
     */
    List<OrderListVo> selectShopOrderList(@Param("offset")Integer offset, @Param("pagesize")Integer pagesize, @Param("cid")Long cid, @Param("key")String key, @Param("oid")Long oid, @Param("shopId") Long shopId,@Param("stateCode")Integer stateCode);

    Integer selectShopOrderListNum(@Param("cid")Long cid, @Param("key")String key, @Param("oid")Long oid, @Param("shopId") Long shopId,@Param("stateCode")Integer stateCode);
}
