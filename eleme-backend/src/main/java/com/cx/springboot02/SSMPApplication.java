package com.cx.springboot02;


import com.cx.springboot02.common.init.AfterServiceInitialize;
import com.cx.springboot02.common.init.BeforeServiceInitialize;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootApplication(scanBasePackages={"com.cx.springboot02.*"})
@MapperScan(basePackages = "com.cx.springboot02.mapper")
@EnableWebSocket//websocket
public class SSMPApplication {
    //Spring应用上下文环境
    public static ConfigurableApplicationContext ctx;
    public static void main(String[] args) {
        //在容器初始化之前
        BeforeServiceInitialize beforeServiceInitialize = new BeforeServiceInitialize();
        beforeServiceInitialize.init();
        SSMPApplication.ctx = SpringApplication.run(SSMPApplication.class, args);
        //容器初始化之后
        AfterServiceInitialize afterServiceInitialize = ctx.getBean(AfterServiceInitialize.class);
        afterServiceInitialize.init();
    }
    /**
     * 获取Spring bean对象
     * 这里重写了bean方法
     * @param name
     * @return Object 一个以所给名字注册的Spring bean的实例
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return ctx.getBean(name);
    }
    public static <T> T getBean(Class<T> requiredType) {
        return ctx.getBean(requiredType);
    }


    //websocket配置
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
