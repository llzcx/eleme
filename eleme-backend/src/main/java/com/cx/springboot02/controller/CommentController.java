package com.cx.springboot02.controller;


import com.cx.springboot02.common.E.ResultCode;
import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.RPage;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.utils.Unobstructed;
import com.cx.springboot02.data.vo.CommentCategory;
import com.cx.springboot02.data.vo.CommentShopVo;
import com.cx.springboot02.data.vo.CommentTotalityInfoVo;
import com.cx.springboot02.data.vo.CommentVo;
import com.cx.springboot02.dto.AddCommentDto;
import com.cx.springboot02.dto.AddShopCommentDto;
import com.cx.springboot02.mapper.CommentImgMapper;
import com.cx.springboot02.pojo.Customer;
import com.cx.springboot02.service.impl.CommentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    @Autowired
    CommentServiceImpl commentService;

    @Autowired
    CommentImgMapper commentImgMapper;

    

    @GetMapping("/totalityRating/{shopId}")
    @Unobstructed
    public JsonResult<Object> get(@PathVariable String shopId){
        CommentTotalityInfoVo totalityRating = commentService.getTotalityRating(Long.valueOf(shopId));
        return ResultTool.success(totalityRating);
    }

  


    @GetMapping("/customer/")
    @Unobstructed
    public JsonResult<RPage<CommentVo>> customerget(@PathParam("pagenum")Integer pagenum,@PathParam("size")Integer size,@PathParam("shopId")String shopId,
                                          @PathParam("haveImg")Boolean haveImg,@PathParam("mingRating")String mingRating){
        if(shopId==null) return ResultTool.fail(ResultCode.PARAM_NOT_VALID);
        Long shopIdL = Long.valueOf(shopId);
        Float mingRatingF = null;
        if(mingRating!=null) mingRatingF = Float.valueOf(mingRating);
        Boolean haveImgB = haveImg;
        RPage<CommentVo> commentList = commentService.getCommentList(pagenum, size, shopIdL, null,mingRatingF, haveImgB);
        return  ResultTool.success(commentList);
    }


    @GetMapping("/shop/")
    @Unobstructed
    public JsonResult<Object> shopget(@PathParam("pagenum")Integer pagenum,@PathParam("size")Integer size,@PathParam("shopId")String shopId,
                                      @PathParam("cid") String customerId,@PathParam("haveImg")Boolean haveImg,@PathParam("mingRating")String mingRating,
    @PathParam("haveReply")Boolean haveReply){
        if(shopId==null) return ResultTool.fail(ResultCode.PARAM_NOT_VALID);
        log.debug("haveImg:[{}],haveReply[{}]",haveImg,haveReply);
        Long shopIdL = Long.valueOf(shopId);
        Float mingRatingF = null;
        if(mingRating!=null) mingRatingF = Float.valueOf(mingRating);
        Boolean haveImgB = haveImg;
        Long customerIdL  = null;
        if(customerId!=null) customerIdL = Long.valueOf(customerId);
        RPage<CommentShopVo> commentList = commentService.getShopCommentList(pagenum, size, shopIdL, customerIdL,mingRatingF, haveImgB,haveReply);
        return  ResultTool.success(commentList);
    }

    @PostMapping("/")
    @Unobstructed
    public JsonResult<Object> add(@RequestBody AddCommentDto addCommentDto){
        Boolean aBoolean = commentService.saveComment(addCommentDto);
        return ResultTool.success(null);
    }


    @DeleteMapping("/")
    @Unobstructed
    public JsonResult<Object> delete(){

        return  null;
    }

}
