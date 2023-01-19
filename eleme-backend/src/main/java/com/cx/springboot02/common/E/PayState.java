package com.cx.springboot02.common.E;

import com.cx.springboot02.common.order.payState.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum PayState {
    /**
     * 支付状态 [必须在等待付款后]
     */
    PAID(101,"已支付", PaidState.class),
    UNPAID(102,"未支付", UnPaidState.class),
    PAID_TIMEOUT(103,"支付超时", PayTimeOutStateState.class),
    /**
     * 物流状态 [必须在等待发货后]
     */
    DISPATCH(202,"正在派送", DispatchState.class),//出仓库
    MAKING(203,"正在制作", MakingState.class),

    /**
     * 订单状态
     */
//    WAITING_FOR_PAYMENT(301,"等待付款"),
//    WAITING_FOR_SHIPPED(302,""),
    CONFIRM_RECEIPT(303,"确认收货等待发货"),
    TRANSACTION_COMPLETION(304,"交易完成",TransactionCompletionState.class),
    ORDER_CANCEL(306,"订单取消",OrderCancelState.class),

    /**
     * 售后状态 [必须在交易关闭以后]
     */
    APPLY_FOR_AFTER_SALES_SERVICE(401,"申请售后"),
    REFUND_COMPLETED(402,"退款完成"),
    NOT_APPLY_FOR_AFTER_SALES_SERVICE(403,"未申请售后"),

    /**
     * 评价状态 [必须在交易关闭以后]
     */
    EVALUATED(501,"已评价"),
    NOT_EVALUATED(502,"未评价"),

    ;
    private static final Logger log = LoggerFactory.getLogger(Logger.class);
    private Integer code;
    private String message;
    private Class cls;

    PayState(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    PayState(Integer code, String message,Class cls) {
        this.code = code;
        this.message = message;
        this.cls = cls;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }

    private static Map<Integer,PayState> payStateHashMap = new HashMap<>();
    private static Map<Integer,Class> payStateClsHashMap = new HashMap<>();
    static {
        for (PayState value : PayState.values()) {
            payStateHashMap.put(value.code,value);
            payStateClsHashMap.put(value.code,value.getCls());
        }
    }

    public static String getMessage(Integer code){
        return payStateHashMap.get(code).getMessage();
    }

    public static PayState getPayState(Integer code){
        return payStateHashMap.get(code);
    }

    public static State getStateCls(Integer code){
        Class cls = payStateClsHashMap.get(code);
        try {
            return (State)cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


}
