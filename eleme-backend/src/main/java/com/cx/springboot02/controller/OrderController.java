package com.cx.springboot02.controller;


import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.cx.springboot02.common.mq.topic.order_cancel.OrderCancelConsumerSchedule;
import com.cx.springboot02.common.mq.topic.order_cancel.OrderCancelProducerSchedule;
import com.cx.springboot02.common.mq.topic.order_timeout.OrderTimeOutProducerSchedule;
import com.cx.springboot02.common.order.Context;
import com.cx.springboot02.common.E.PayState;
import com.cx.springboot02.common.E.ResultCode;
import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.RPage;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.order.payState.*;
import com.cx.springboot02.common.pay.AliBean;
import com.cx.springboot02.common.pay.AliPay;
import com.cx.springboot02.common.pay.AliReturnPayBean;
import com.cx.springboot02.common.pay.PayUtil;
import com.cx.springboot02.common.utils.CommonBeanUtils;
import com.cx.springboot02.common.utils.Unobstructed;
import com.cx.springboot02.data.vo.OrderListVo;
import com.cx.springboot02.data.vo.PayInfoVo;
import com.cx.springboot02.dto.SaveOrderDto;
import com.cx.springboot02.pojo.Order;
import com.cx.springboot02.service.impl.AddressServiceImpl;
import com.cx.springboot02.service.impl.CustomerServiceImpl;
import com.cx.springboot02.service.impl.OrderServiceImpl;
import com.cx.springboot02.service.impl.PayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

import static com.cx.springboot02.common.mq.MessageQueueFinal.ORDER_CANCEL_TOPIC;
import static com.cx.springboot02.common.mq.MessageQueueFinal.ORDER_TIMEOUT_TOPIC;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 陈翔
 * @since 2022-10-05
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {


    @Autowired
    OrderServiceImpl orderService;


    @Autowired
    CustomerServiceImpl customerService;

    @Autowired
    AddressServiceImpl addressService;

    @Autowired
    OrderTimeOutProducerSchedule orderTimeOutProducerSchedule;

    @Autowired
    OrderCancelProducerSchedule orderCancelProducerSchedule;


    @PostMapping("/save")
    @Unobstructed
    public JsonResult<Object> saveOrder(@RequestBody SaveOrderDto saveOrderDto) {
        try {
            Long aLong = orderService.saveO(saveOrderDto);
            //将订单id返回
            return ResultTool.success(aLong);
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResultTool.fail(ResultCode.PARAM_NOT_VALID);
        }
    }


    /**
     * 支付接口
     *
     * @param mp
     * @return
     */
    @PutMapping("/pay")
    @Unobstructed
    public JsonResult<Order> pay(@RequestBody Map<String, Object> mp) {
        try {
            Long id = Long.valueOf(((Integer) mp.get("oid")));
            Context context = new Context(id);
            PaidState paidState = new PaidState();
            if(paidState.doAction(context)){
                Order byId = orderService.getById(id);
                return ResultTool.success(byId);
            }else{
                return ResultTool.fail(ResultCode.ORDER_UPDATE_EXCEPTION);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResultTool.fail(ResultCode.PARAM_NOT_VALID);
        }
    }


    /**
     * 根据订单id获取到某个订单
     *
     * @param mp
     * @return
     */
    @GetMapping("/{id}")
    @Unobstructed
    public JsonResult<Object> gettime(@RequestBody Map<String, Object> mp, @PathVariable String id) {
        Long oid = Long.valueOf(id);
        Order order = orderService.getById(oid);
        return ResultTool.success(order);
    }

    /**
     * 前台[客户端]
     * 根据用户id获取到订单列表 分页处理
     *
     * @param pagenum 第几页
     * @param size    每页多少条数据
     * @param key     关键词 可以为空
     * @param cid     用户id 可以为空
     * @param oid     订单id
     * @param shopId
     * @return
     */
    @GetMapping("/list/")
    @Unobstructed
    public JsonResult<RPage<OrderListVo>> getList(@PathParam("pagenum") Integer pagenum, @PathParam("size") Integer size,
                                                  @PathParam("key") String key, @PathParam("cid") String cid, @PathParam("oid") String oid, @PathParam("shopId") String shopId) {
        log.info("{} {} {} {} {} {} ", pagenum, size, key, cid, oid, shopId);
        if (pagenum == null || size == null || cid == null) return ResultTool.fail(ResultCode.FIELD_IS_EMPTY);
        Long oidl = null;
        if (oid != null) oidl = Long.valueOf(oid);

        Long shopIdl = null;
        if (shopId != null) shopIdl = Long.valueOf(shopId);

        Long cidl = Long.valueOf(cid);

        RPage<OrderListVo> orderListPage = orderService.getOrderListPage(pagenum, size, cid, key, oidl, shopIdl);
        return ResultTool.success(orderListPage);
    }

    /**
     * 后台[商家端]
     * 根据用户id获取到订单列表 分页处理
     *
     * @param pagenum 第几页
     * @param size    每页多少条数据
     * @param key     关键词 可以为空
     * @param cid     用户id 可以为空
     * @param oid     订单id
     * @param shopId
     * @return
     */
    @GetMapping("/shopList/")
    @Unobstructed
    public JsonResult<RPage<OrderListVo>> shopGetList(@PathParam("pagenum") Integer pagenum, @PathParam("size") Integer size,
                                                      @PathParam("key") String key, @PathParam("cid") String cid, @PathParam("oid") String oid, @PathParam("shopId") String shopId, @Param("statecode") String statecode) {
        log.info("pagenum{},size{},key{},cid{},oid{},shoId{}", pagenum, size, key, cid, oid, shopId);
        if (pagenum == null || size == null) return ResultTool.fail(ResultCode.FIELD_IS_EMPTY);
        Long oidl = null;
        if (oid != null) oidl = Long.valueOf(oid);

        Long shopIdl = null;
        if (shopId != null) shopIdl = Long.valueOf(shopId);

        Long cidl = null;
        if (cid != null) cidl = Long.valueOf(cid);

        Integer statecodel = null;
        if (statecode != null) statecodel = Integer.valueOf(statecode);

        RPage<OrderListVo> orderListPage = orderService.selectShopOrderList(pagenum, size, cidl, key, oidl, shopIdl, statecodel);
        return ResultTool.success(orderListPage);
    }


    /**
     * 后台[管理员端]
     * 根据用户id获取到订单列表 分页处理
     *
     * @param pagenum 第几页
     * @param size    每页多少条数据
     * @param key     关键词 可以为空
     * @param cid     用户id 可以为空
     * @param oid     订单id
     * @param shopId
     * @return
     */
    @GetMapping("/ManageList/")
    @Unobstructed
    public JsonResult<RPage<OrderListVo>> ManageGetList(@PathParam("pagenum") Integer pagenum, @PathParam("size") Integer size,
                                                      @PathParam("key") String key, @PathParam("cid") String cid, @PathParam("oid") String oid, @PathParam("shopId") String shopId, @Param("statecode") String statecode) {
        log.info("pagenum{},size{},key{},cid{},oid{},shoId{}", pagenum, size, key, cid, oid, shopId);
        if (pagenum == null || size == null) return ResultTool.fail(ResultCode.FIELD_IS_EMPTY);
        Long oidl = null;
        if (oid != null) oidl = Long.valueOf(oid);

        Long shopIdl = null;
        if (shopId != null) shopIdl = Long.valueOf(shopId);

        Long cidl = null;
        if (cid != null) cidl = Long.valueOf(cid);

        Integer statecodel = null;
        if (statecode != null) statecodel = Integer.valueOf(statecode);

        RPage<OrderListVo> orderListPage = orderService.selectManageOrderList(pagenum, size, cidl, key, oidl, shopIdl, statecodel);
        return ResultTool.success(orderListPage);
    }


    /**
     * 获取已支付的订单列表
     *
     * @return
     */
    @GetMapping("/payState/havePay")
    @Unobstructed
    public JsonResult<List<Order>> getHavePayOrder() {
        return ResultTool.success(orderService.getPayStateOrderList(PayState.PAID));
    }


    /**
     * 获取正在派送的订单列表
     *
     * @return
     */
    @GetMapping("/payState/Dispatch")
    @Unobstructed
    public JsonResult<List<Order>> getDispatchOrder() {
        return ResultTool.success(orderService.getPayStateOrderList(PayState.PAID));
    }


    /**
     * 获取已完成的订单列表
     *
     * @return
     */
    @GetMapping("/payState/finish")
    @Unobstructed
    public JsonResult<List<Order>> getFinishorderList() {
        return ResultTool.success(orderService.getPayStateOrderList(PayState.TRANSACTION_COMPLETION));
    }

    /**
     * 商家接单 将状态从 已支付状态 => 正在准备状态
     *
     * @return
     */
    @PutMapping("/orderReceiving/{oid}")
    @Unobstructed
    public JsonResult<Object> orderMaking(@PathVariable String oid) {
        Long id = Long.valueOf(oid);
        MakingState makingState = new MakingState();
        Context context = new Context(id);
        if(makingState.doAction(context)){
            return ResultTool.success();
        }else{
            return ResultTool.fail(ResultCode.ORDER_UPDATE_EXCEPTION);
        }

    }

    /**
     * 商家取消订单 将订单状态:已支付状态 => 订单取消 售后状态:退款成功
     *
     * @return
     */
    @PutMapping("/CancelOrderReceiving/{oid}")
    @Unobstructed
    public JsonResult<Object> CancelOrderReceiving(@PathVariable String oid) {
        Long id = Long.valueOf(oid);
        OrderCancelState cancelState = new OrderCancelState();
        Context context = new Context(id);
        if(cancelState.doAction(context)){
            return ResultTool.success();
        }else{
            return ResultTool.fail(ResultCode.ORDER_UPDATE_EXCEPTION);
        }
    }

    /**
     * 商家接单 将状态从 正在准备状态 => 正在派送状态
     *
     * @return
     */
    @PutMapping("/orderDispatching/{oid}")
    @Unobstructed
    public JsonResult<Object> orderDispatching(@PathVariable String oid) {
        log.info("正在改变订单[{}]状态 =>",oid);
        Long id = Long.valueOf(oid);
        DispatchState dispatchState = new DispatchState();
        Context context = new Context(id);
        if (dispatchState.doAction(context)) {
            return ResultTool.success();
        } else {
            return ResultTool.fail(ResultCode.ORDER_UPDATE_EXCEPTION);
        }
    }


    /**
     * 商家接单 将状态从 正在派送状态 => 订单完成
     *
     * @return
     */
    @PutMapping("/orderFinish/{oid}")
    @Unobstructed
    public JsonResult<Object> orderFinish(@PathVariable String oid) {
        Long id = Long.valueOf(oid);
        Context context = new Context(id);
        TransactionCompletionState tcs = new TransactionCompletionState();
        if (tcs.doAction(context)) {
            return ResultTool.success();
        } else {
            return ResultTool.fail(ResultCode.ORDER_UPDATE_EXCEPTION);
        }
    }



    @Autowired
    private PayServiceImpl payService;


    /**
     * 支付接口 [alibaba]
     *
     * @param outTradeNo
     * @param subject
     * @param totalAmount
     * @param body
     * @return
     * @throws AlipayApiException
     */
    @PostMapping(value = "/alipay")
    @Unobstructed
    public String alipay(String outTradeNo, String subject, String totalAmount, String body) throws AlipayApiException {
//        outTradeNo 为订单id的字符串形式
        AliBean alipayBean = new AliBean();
        alipayBean.setOut_trade_no(outTradeNo);
        alipayBean.setSubject(subject);
        alipayBean.setTotal_amount(totalAmount);
        alipayBean.setBody(body);
        return payService.aliPay(alipayBean);
    }

    @Autowired
    private AliPay aliPay;


    /**
     * 支付回调接口
     *
     * @param request
     * @param response
     * @param returnPay
     * @throws Exception
     */
    @PostMapping("/notify")  // 注意这里必须是POST接口
    @Unobstructed
    public void payNotify(HttpServletRequest request, HttpServletResponse response, AliReturnPayBean returnPay) throws Exception {
        response.setContentType("type=text/html;charset=UTF-8");
        log.info("****************************************支付宝的的回调函数被调用******************************");
        if (!PayUtil.checkSign(request)) {
            log.info("****************************************验签失败*******************************************");
            response.getWriter().write("failture");
            return;
        }
        if (returnPay == null) {
            log.info("支付宝的returnPay返回为空");
            response.getWriter().write("error");
            return;
        }
        log.info("支付宝的returnPay" + returnPay.toString());
        if (returnPay.getTrade_status().equals("TRADE_SUCCESS")) {
            //检查是否处于未支付状态

            log.info("支付宝的支付状态为TRADE_SUCCESS");
//            tbPaymentRecordsService.aliPaySuccess(returnPay);
            String out_trade_no = returnPay.getOut_trade_no();
            String total_amount = returnPay.getTotal_amount();
            try {
                Long id = Long.valueOf(out_trade_no);
                Context context = new Context(id);
                PaidState paidState = new PaidState();
                if (paidState.doAction(context)){
                    Order order = new Order();
                    //设置真实支付金额
                    order.setRealPay(Float.valueOf(total_amount));
                    order.setId(id);
                    orderService.updateById(order);
                    response.getWriter().write("付款成功");
                    //发布MQ延时消息 5分钟后如果商家不接单 直接取消订单
                    OrderCancelConsumerSchedule.Data data = new OrderCancelConsumerSchedule.Data();
                    data.setOrder(order);
                    data.setAlipayBean(returnPay);
                    orderCancelProducerSchedule.send(ORDER_CANCEL_TOPIC, JSONObject.toJSONString(data));
                }else{
                    //退款
                    payService.aliRefund(returnPay);
                    response.getWriter().write("订单状态异常,退款将在3个工作日内返还到您的账户");
                }

            } catch (Exception exception) {
                exception.printStackTrace();
                response.getWriter().write("error");
            }
        }
    }


    /**
     * 获取订单状态
     *
     * @param oid
     * @return
     */
    @GetMapping("/checkPayState/{oid}")
    @Unobstructed
    public JsonResult<Object> checkPayState(@PathVariable String oid) {
        Long oidL = Long.valueOf(oid);
        Order order = orderService.getById(oidL);
        Integer code = order.getOrderState();
        return ResultTool.success(code);
    }


    /**
     * 拉取付款信息
     *
     * @param oid
     * @return
     */
    @GetMapping("/payInfo/{oid}")
    @Unobstructed
    public JsonResult<PayInfoVo> getPayInfo(@PathVariable String oid) {
        Long oidL = Long.valueOf(oid);
        Order order = orderService.getById(oidL);
        if (order != null) {
            PayInfoVo payInfoVo = new PayInfoVo();
            CommonBeanUtils.copyProperties(order, payInfoVo);
            return ResultTool.success(payInfoVo);
        } else {
            return ResultTool.fail(ResultCode.PARAM_NOT_VALID);
        }
    }


}

