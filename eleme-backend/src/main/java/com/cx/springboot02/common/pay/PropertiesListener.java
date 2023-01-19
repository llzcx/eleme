package com.cx.springboot02.common.pay;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PropertiesListener implements ApplicationListener<ApplicationStartedEvent> {
 
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        AliPayProperties.loadProperties();
    }
}