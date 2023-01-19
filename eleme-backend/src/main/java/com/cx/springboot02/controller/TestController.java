package com.cx.springboot02.controller;

import com.cx.springboot02.common.mq.topic.order_timeout.OrderTimeOutProducerSchedule;
import com.cx.springboot02.common.utils.Unobstructed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private OrderTimeOutProducerSchedule orderTImeOutProducerSchedule;
    
    @GetMapping("/push")
    @Unobstructed
    public void pushMessageToMQ() {
        orderTImeOutProducerSchedule.send("Topic", "Coisini");
    }

}