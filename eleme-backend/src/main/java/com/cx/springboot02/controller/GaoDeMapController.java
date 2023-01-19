package com.cx.springboot02.controller;


import com.cx.springboot02.common.E.ResultCode;
import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.utils.Unobstructed;
import com.cx.springboot02.service.impl.GaoDeMapServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;


@RestController
@RequestMapping("/gaoDe")
@Slf4j
public class GaoDeMapController {


    @Autowired
    GaoDeMapServiceImpl gaoDeMapService;
    
    
    @GetMapping("/getInfoByKey")
    @Unobstructed
    public String get(@PathParam("keyword")String keyword){
        String res = null;
        try {
            res = gaoDeMapService.getTips(keyword);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return  res;
    }

    @GetMapping("/regeo")
    @Unobstructed
    public String geohash(@PathParam("geohash")String geohash){
        String res = null;
        try {
            res = gaoDeMapService.regeo(geohash);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return  res;
    }

    @GetMapping("/city")
    @Unobstructed
    public JsonResult city(@PathParam("geohash")String geohash) throws Exception {
        String city = gaoDeMapService.city(geohash);
        if(city!=null){
            return ResultTool.success(city);
        }else{
            return  ResultTool.fail(ResultCode.COMMON_FAIL);
        }

    }
}
