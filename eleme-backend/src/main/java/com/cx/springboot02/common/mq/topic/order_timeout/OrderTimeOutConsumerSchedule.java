package com.cx.springboot02.common.mq.topic.order_timeout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.cx.springboot02.common.mq.MessageQueueFinal;
import com.cx.springboot02.common.order.Context;
import com.cx.springboot02.common.order.payState.OrderCancelState;
import com.cx.springboot02.common.order.payState.PayTimeOutStateState;
import com.cx.springboot02.common.pay.AliReturnPayBean;
import com.cx.springboot02.common.utils.DateUtils;
import com.cx.springboot02.pojo.Order;
import com.cx.springboot02.service.impl.OrderServiceImpl;
import com.cx.springboot02.service.impl.PayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Description MQ消费者
 *      CommandLineRunner 初始化预加载数据
 * @author coisini
 * @date Aug 25, 2021
 * @Version 1.0
 */
@Component
@Slf4j
public class OrderTimeOutConsumerSchedule implements CommandLineRunner {

    @Autowired
    PayServiceImpl payService;

    private String consumerGroup = MessageQueueFinal.ORDER_TIMEOUT_CONSUMER_GROUP;

    @Value("${rocketmq.namesrv-addr}")
    private String nameSrvAddr;

    @Autowired
    OrderServiceImpl orderService;



    public void messageListener() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(this.consumerGroup);
        consumer.setNamesrvAddr(this.nameSrvAddr);

        /**
         * 订阅主题
         */
        consumer.subscribe(MessageQueueFinal.ORDER_TIMEOUT_TOPIC, "*");

        /**
         * 设置消费消息数
         */
        consumer.setConsumeMessageBatchMaxSize(1);

        /**
         * 设置消费模式
         */
        consumer.setMessageModel(MessageModel.BROADCASTING);//设置广播消费模式
        /**
         * 注册消息监听
         */
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
            for (Message message : messages) {
                System.out.println("["+ DateUtils.getCurrentTime() +"]监听到消息：" + new String(message.getBody()));
                String str = new String(message.getBody());
                JSONObject jsonObject = JSON.parseObject(str);
                Order order = JSON.toJavaObject(jsonObject, Order.class);
                Context context1 = new Context(order.getId());
                PayTimeOutStateState payTimeOutStateState = new PayTimeOutStateState();
                if(payTimeOutStateState.doAction(context1)){
                    log.info("订单"+order.getId()+"已经成功被消费[订单超时]{}",order);
                }else{
                    log.info("订单"+order.getId()+"已经成功被消费[已支付]{}",order);
                }

            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
    }


    @Override
    public void run(String... args) throws Exception {

    }
}
