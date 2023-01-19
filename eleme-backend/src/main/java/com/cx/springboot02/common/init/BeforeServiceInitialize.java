package com.cx.springboot02.common.init;


import com.cx.springboot02.common.E.PayState;
import org.springframework.stereotype.Component;

public class BeforeServiceInitialize {


    public void init(){
        //加载一次静态块
        PayState payState = PayState.PAID;

    }
}
