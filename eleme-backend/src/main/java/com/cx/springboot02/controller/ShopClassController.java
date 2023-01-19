package com.cx.springboot02.controller;


import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.utils.Unobstructed;
import com.cx.springboot02.pojo.ShopClass;
import com.cx.springboot02.service.impl.ShopClassServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shopclass")
@Slf4j
public class ShopClassController {


    @Autowired
    ShopClassServiceImpl shopClassServices;


    @GetMapping("")
    @Unobstructed
    public JsonResult<List<ShopClass>> get(){
        return ResultTool.success(shopClassServices.getAll());
    }


}
