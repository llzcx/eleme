package com.cx.springboot02.common.mq;

public interface MessageQueueFinal {
    String ORDER_TIMEOUT_TOPIC = "order_timeout";
    String ORDER_TIMEOUT_PRODUCER_GROUP = "order_timeout_producer_group";
    String ORDER_TIMEOUT_CONSUMER_GROUP = "order_timeout_consumer_group";

    String ORDER_CANCEL_TOPIC = "order_cancel";
    String ORDER_CANCEL_CONSUMER_GROUP = "order_cancel_consumer_group";
    String ORDER_CANCEL_PRODUCER_GROUP = "order_cancel_producer_group";
}
