package com.cx.springboot02.common.init;


import com.cx.springboot02.common.elasticsearch.ShopListSearchDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AfterServiceInitialize {
    @Autowired
    ShopListSearchDao shopListSearchDao;

    public void init(){
        try {
            shopListSearchDao.initESData();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
