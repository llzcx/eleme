package com.cx.springboot02.controller;


import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.RPage;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.utils.Unobstructed;
import com.cx.springboot02.data.vo.ChatCustomerListVo;
import com.cx.springboot02.data.vo.CustomerBusinessMsgVo;
import com.cx.springboot02.mapper.CustomerBusinessMsgMapper;
import com.cx.springboot02.service.impl.CustomerBusinessMsgServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * <p>
 *  前端控制器 [客户端与商家端发消息]
 * </p>
 *
 * @author 陈翔
 * @since 2022-10-05
 */
@RestController
@RequestMapping("/customerBusinessMsg")
@Slf4j
public class CustomerBusinessMsgController {

    @Autowired
    CustomerBusinessMsgServiceImpl customerBusinessMsgService;

    @Autowired
    CustomerBusinessMsgMapper customer_businessMsgMapper;

    /**
     *  获取到消息列表
     * @return
     */
    @GetMapping("/list")
    @Unobstructed
    public JsonResult<Object> getList(@PathParam("pagenum")Integer pagenum,@PathParam("size")Integer size,@PathParam("cid")String cid,@PathParam("bid")String bid){
        log.info("参数列表: pagenum:{} size:{} cid:{} bid:{} ",pagenum,size,cid,bid);
        Long cidL = Long.valueOf(cid);
        Long bidL = Long.valueOf(bid);
        RPage<CustomerBusinessMsgVo> list = customerBusinessMsgService.getList(pagenum, size, cidL, bidL);
        return ResultTool.success(list);
    }


    /**
     * 根据商家id获取到聊天对象列表
     * @param bid
     * @return
     */
    @GetMapping("/customer/list/{bid}")
    @Unobstructed
    public JsonResult<Object> getCustomerList(@PathVariable String bid){
        List<ChatCustomerListVo> customerList = customer_businessMsgMapper.getCustomerList(Long.valueOf(bid));
        return ResultTool.success(customerList);
    }



}

