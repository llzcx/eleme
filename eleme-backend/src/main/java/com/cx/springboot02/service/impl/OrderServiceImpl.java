package com.cx.springboot02.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.common.E.PayState;
import com.cx.springboot02.common.RPage;
import com.cx.springboot02.common.mq.topic.order_timeout.OrderTimeOutProducerSchedule;
import com.cx.springboot02.common.utils.DateUtils;
import com.cx.springboot02.data.vo.OrderListVo;
import com.cx.springboot02.dto.BuyCartDataDto;
import com.cx.springboot02.dto.SaveOrderDto;
import com.cx.springboot02.mapper.*;
import com.cx.springboot02.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cx.springboot02.common.mq.MessageQueueFinal.ORDER_TIMEOUT_TOPIC;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 陈翔
 * @since 2022-10-05
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>  {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    BusinessMapper businessMapper;
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    FoodMapper foodMapper;

    @Autowired
    AddressMapper addressMapper;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    private OrderTimeOutProducerSchedule orderTImeOutProducerSchedule;


    @Autowired
    Order_detailsMapper order_detailsMapper;

    @Autowired
    OrderDetailServiceImpl orderDetailService;

    @Autowired
    SpecsMapper specsMapper;

    @Autowired
    SpecsServiceImpl specsService;



    /**
     * 更新订单状态
     * @param order
     * @param payState
     */
    public void updatePayById(Order order, PayState payState){
        order.setOrderState(payState.getCode());
        orderMapper.updateOrder(order);
    }

    /**
     * 保存订单信息
     * @param saveOrderDto
     */
    public Long saveO(SaveOrderDto saveOrderDto){
        Order order = new Order();
        order.setCreateTime(DateUtils.getCurrentTime());
        //设置为未支付
        order.setOrderState(PayState.UNPAID.getCode());
        Customer customer = customerMapper.selectById(saveOrderDto.getCustomerId());
        order.setPhone(customer.getPhoneNumber());
        Address address = addressMapper.selectById(saveOrderDto.getAddressId());
        order.setAddressName(address.getAddress()+address.getSpecificAddress());
        order.setConsignee(address.getName());
        order.setGeohash(address.getGeohash());
        order.setCustomerId(customer.getId());
        order.setRemarks(saveOrderDto.getRemarks());
        order.setShopId(saveOrderDto.getShopId());
        order.setCommentState(false);
        order.setAfterSalesStatus(PayState.NOT_APPLY_FOR_AFTER_SALES_SERVICE.getCode());
        Business business = businessMapper.selectById(saveOrderDto.getShopId());
        order.setShopAddress(business.getAddress());
        order.setFloatDeliveryFee(business.getFloatDeliveryFee());
        order.setShopGeohash(business.getLongitude()+","+business.getLatitude());
        orderMapper.insert(order);
        //根据购物车id去找到所有的购物车数据
        Float total = 0f;
        for (BuyCartDataDto buyCartDataDto : saveOrderDto.getList()) {
            //拿到skuId
            Long skuId = buyCartDataDto.getSkuId();
            //拿到sku
            Food food = foodMapper.selectBySkuId(skuId);
            //拿到spu
            Goods goods = goodsMapper.selectById(food.getGoodsId());
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setOrderId(order.getId());
            orderDetails.setGoodsName(goods.getName());
            String specsList = food.getSpecsList();
            String strByIds = specsService.getStrByIds(specsList);
            orderDetails.setSpecsList(strByIds);
            orderDetails.setPackingFee(food.getPackingFee());
            orderDetails.setPrice(food.getPrice());
            orderDetails.setNum(buyCartDataDto.getNum());
            orderDetails.setImagePath(goods.getImagePath());
            orderDetails.setSkuId(buyCartDataDto.getSkuId());
            order_detailsMapper.insert(orderDetails);
            Float fee = orderDetails.getPackingFee()==null?0:orderDetails.getPackingFee();
            total += orderDetails.getPrice()*orderDetails.getNum()+fee;
        }
        total += business.getFloatDeliveryFee();
        order.setTotalPrice(total);
        orderMapper.updateById(order);
        //生产者生产推送消息 超时未支付订单 自动取消
        orderTImeOutProducerSchedule.send(ORDER_TIMEOUT_TOPIC, JSONObject.toJSONString(order));
        return order.getId();
    }


    /**
     * 更新售后状态
     * @param order
     * @param payState
     */
    public void updateAfterSalesStatusById(Order order, PayState payState){
        order.setAfterSalesStatus(payState.getCode());
        orderMapper.updateOrder(order);
    }

    /**
     * 更新评论状态
     * @param order
     * @param payState
     */
    public void updateCommentStatusById(Order order, Boolean payState){
        order.setCommentState(payState);
        orderMapper.updateOrder(order);
    }


    /**
     * 对订单vo进行加工
     * @param rows
     */
    public  void modificationShopAndClientOrderListVo(List<OrderListVo> rows){
        for (OrderListVo row : rows) {
            Float totalPrice = 0f;
            row.setOrderStateStr(PayState.getMessage(row.getOrderState()));
            List<OrderDetails> orderDetailList = orderDetailService.getOrderDetailByOId(row.getId());
            row.setOrderDetailsList(orderDetailList);
        }
    }

    public void modificationManageOrderListVo(List<OrderListVo> rows){
        for (OrderListVo row : rows) {
            row.setOrderStateStr(PayState.getMessage(row.getOrderState()));
            List<OrderDetails> orderDetailList = orderDetailService.getOrderDetailByOId(row.getId());
            row.setOrderDetailsList(orderDetailList);
        }
    }

    /**
     * 获取客户端页面的订单列表 包含了其中的商品信息 spu和sku
     * @param pagenum
     * @param size
     * @param cid
     * @param key
     * @return
     */
    public RPage<OrderListVo> getOrderListPage(Integer pagenum, Integer size, String cid, String key,Long oid,Long shopId){
        RPage<OrderListVo> orderListDtoRPage = new RPage<>(pagenum, size, orderMapper.selectOrderListByCId(size * (pagenum - 1), size, Long.valueOf(cid), key,oid,shopId));
        //对dto进行修饰
        modificationShopAndClientOrderListVo(orderListDtoRPage.getRows());
        return  orderListDtoRPage;
    }


    /**
     * 获取商家端页面的订单列表 包含了其中的商品信息 spu和sku
     * @param pagenum
     * @param size
     * @param cid
     * @param key
     * @return
     */
    public RPage<OrderListVo> selectShopOrderList(Integer pagenum, Integer size, Long cid, String key, Long oid, Long shopId,Integer stateCode){
        RPage<OrderListVo> orderListDtoRPage = new RPage<>(pagenum, size, orderMapper.selectShopOrderList(size * (pagenum - 1), size, cid, key,oid,shopId,stateCode));
        orderListDtoRPage.SetTotalCountAndTotalPage(orderMapper.selectShopOrderListNum(cid, key,oid,shopId,stateCode));
        modificationShopAndClientOrderListVo(orderListDtoRPage.getRows());

        return  orderListDtoRPage;
    }

    /**
     * 获取管理端页面的订单列表 包含了其中的商品信息 spu和sku
     * @param pagenum
     * @param size
     * @param cid
     * @param key
     * @return
     */
    public RPage<OrderListVo> selectManageOrderList(Integer pagenum, Integer size, Long cid, String key, Long oid, Long shopId,Integer stateCode){
        RPage<OrderListVo> orderListDtoRPage = new RPage<>(pagenum, size, orderMapper.selectShopOrderList(size * (pagenum - 1), size, cid, key,oid,shopId,stateCode));
        orderListDtoRPage.SetTotalCountAndTotalPage(orderMapper.selectShopOrderListNum(cid, key,oid,shopId,stateCode));
        modificationManageOrderListVo(orderListDtoRPage.getRows());
        return  orderListDtoRPage;
    }

    /**
     * 获取指定支付状态的列表
     * @param payState
     */
    public List<Order> getPayStateOrderList(PayState payState){
        Map<String,Object> mp = new HashMap<>();
        mp.put("order_state",payState.getCode());
        List<Order> orders = orderMapper.selectByMap(mp);
        return orders;
    }

    /**
     * 根据四级获取到订单状态
     * @param id
     * @return
     */
    public PayState getOrderState(Long id){
        Order order = orderMapper.selectById(id);
        return PayState.getPayState(order.getOrderState());

    }



}
