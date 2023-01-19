package com.cx.springboot02.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.common.E.ShopCheckNum;
import com.cx.springboot02.common.RPage;
import com.cx.springboot02.common.utils.DateUtils;
import com.cx.springboot02.common.utils.StringUtil;
import com.cx.springboot02.data.vo.*;
import com.cx.springboot02.dto.AddCommentDto;
import com.cx.springboot02.dto.AddShopCommentDto;
import com.cx.springboot02.mapper.*;
import com.cx.springboot02.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> {

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    CommentImgMapper commentImgMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    CustomerMapper customerMapper;

    /**
     * 修饰vo
     * @param commentVo
     */
    public void modificationCommentVo(CommentVo commentVo){
        Customer customer = customerMapper.selectById(commentVo.getCustomerId());
        if(customer!=null){
            commentVo.setCustomerImg(customer.getAvatar());
            commentVo.setCustomerName(customer.getName());
        }else{
            commentVo.setCustomerImg("https://elm.cangdu.org/img/default.jpg");
            commentVo.setCustomerName("饿了么用户");
        }
        Map<String,Object> mp = new HashMap<>();
        mp.put("comment_id",commentVo.getId());
        List<CommentImg> commentImgs = commentImgMapper.selectByMap(mp);
        List<String> imags = new ArrayList<>();
        for (CommentImg commentImg : commentImgs) {
            imags.add(commentImg.getImagePath());
        }
        commentVo.setImages(imags);
        Map<String,Object> mp2 = new HashMap<>();
        mp2.put("parent", commentVo.getId());
        List<Comment> comment = commentMapper.selectByMap(mp2);
        commentVo.setHaveReplay(comment.size()!=0);
        if(comment.size()>0){
            commentVo.setChildComment(comment.get(0));
            commentVo.setHaveReplayStr("已回复");
        }else{
            commentVo.setHaveReplayStr("未回复");
        }

    }

    /**
     * 修饰返回给商家端
     * @param commentShopVo
     */
    public void modificationShopCommentVo(CommentShopVo commentShopVo){
        Customer customer = customerMapper.selectById(commentShopVo.getCustomerId());
        Map<String,Object> mp2 = new HashMap<>();
        mp2.put("parent", commentShopVo.getId());
        List<Comment> comment = commentMapper.selectByMap(mp2);
        commentShopVo.setHaveReplay(comment.size()!=0);
        if(comment.size()>0){
            commentShopVo.setChildComment(comment.get(0));
            commentShopVo.setHaveReplayStr("已回复");
        }else{
            commentShopVo.setHaveReplayStr("未回复");
        }

        if(customer!=null){
            commentShopVo.setCustomerImg(customer.getAvatar());
            commentShopVo.setCustomerName(customer.getName());
        }else{
            commentShopVo.setCustomerImg("https://elm.cangdu.org/img/default.jpg");
            commentShopVo.setCustomerName("饿了么用户");
        }
        Map<String,Object> mp = new HashMap<>();
        mp.put("comment_id",commentShopVo.getId());
        List<CommentImg> commentImgs = commentImgMapper.selectByMap(mp);
        List<String> imags = new ArrayList<>();
        for (CommentImg commentImg : commentImgs) {
            imags.add(commentImg.getImagePath());
        }
        commentShopVo.setImages(imags);
    }


    public CommentTotalityInfoVo getTotalityRating(Long shopId){
        CommentTotalityInfoVo commentTotalityInfoVo = new CommentTotalityInfoVo();
        QueryWrapper<Comment> sectionQueryWrapper = new QueryWrapper<>();
        sectionQueryWrapper.eq("shop_id", shopId);
        Integer integer = commentMapper.selectCount(sectionQueryWrapper);
        CommentCategory commentCategory = new CommentCategory();
        commentCategory.setAll(integer);
        commentTotalityInfoVo.setCommentCategory(commentCategory);
        commentTotalityInfoVo.setTotalRating(4.6f);
        commentTotalityInfoVo.setAboveAmbient(0.769f);
        commentTotalityInfoVo.setServiceAttitudeScore(4.7f);
        commentTotalityInfoVo.setFoodScore(4.8f);
        commentTotalityInfoVo.setArriveTime(30);
        return commentTotalityInfoVo;
    }


    /**
     * 保存一个评论
     * @param addCommentDto
     * @return
     */
    public Boolean saveComment(AddCommentDto addCommentDto){
        Comment comment = new Comment();
        BeanUtils.copyProperties(addCommentDto, comment);
        comment.setCreateTime(DateUtils.getCurrentTime());

        //根据订单id去将对应的订单状态更新
        Order order = new Order();
        order.setId(addCommentDto.getOrderId());
        order.setCommentState(true);
        orderMapper.updateById(order);
        //根据订单id找到对应的商铺id
        Order order1 = orderMapper.selectById(addCommentDto.getOrderId());
        Long shopId = order1.getShopId();
        comment.setShopId(shopId);
        commentMapper.insert(comment);
        //更新订单
        if(addCommentDto.getImgList()!=null){
            for (String s : addCommentDto.getImgList()) {
                CommentImg commentImg = new CommentImg();
                commentImg.setImagePath(s);
                commentImg.setCommentId(comment.getId());
                commentImgMapper.insert(commentImg);
            }
        }
        return true;
    }



    public Boolean deleteComment(Long commentId){
        //删除这个评论以及所有含有这个评论的评论
        Map<String,Object>  mp1 = new HashMap<>();
        mp1.put("parent",commentId);
        commentMapper.deleteByMap(mp1);
        mp1.clear();
        mp1.put("top_parent", commentId);
        commentMapper.deleteByMap(mp1);

        //删除所有的图片
        Map<String,Object> mp = new HashMap<>();
        mp.put("comment_id",commentId);
        commentImgMapper.deleteByMap(mp);
        return true;
    }


    /**
     * 获取客户端评论列表
     * @param pagenum
     * @param size
     * @param shopId
     * @param cid
     * @param MingRating
     * @param haveImg
     * @return
     */
    public RPage<CommentVo> getCommentList(Integer pagenum, Integer size,Long shopId,Long cid,Float MingRating,Boolean haveImg){
        RPage<CommentVo> rPage = new RPage<>(pagenum, size, commentMapper.selectCommentList(size * (pagenum - 1), size, shopId, cid, MingRating,haveImg));
        rPage.SetTotalCountAndTotalPage(commentMapper.selectCommentListCount(shopId, cid, MingRating, haveImg));
        for (CommentVo row : rPage.getRows()) {
            modificationCommentVo(row);
        }
        return  rPage;
    }

    /**
     * 获取商家端评论列表
     * @param pagenum
     * @param size
     * @param shopId
     * @param cid
     * @param MingRating
     * @param haveImg
     * @return
     */
    public RPage<CommentShopVo> getShopCommentList(Integer pagenum, Integer size,Long shopId,Long cid,Float MingRating,Boolean haveImg,Boolean haveReply){
        RPage<CommentShopVo> rPage = new RPage<>(pagenum, size, commentMapper.selectShopCommentList(size * (pagenum - 1), size, shopId, cid, MingRating,haveImg,haveReply));
        rPage.SetTotalCountAndTotalPage(commentMapper.selectShopCommentListCount(shopId, cid, MingRating, haveImg,haveReply));
        for (CommentShopVo row : rPage.getRows()) {
            modificationShopCommentVo(row);
        }
        return  rPage;
    }

}
