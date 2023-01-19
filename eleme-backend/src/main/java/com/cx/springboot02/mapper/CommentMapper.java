package com.cx.springboot02.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cx.springboot02.data.vo.CommentShopVo;
import com.cx.springboot02.data.vo.CommentVo;
import com.cx.springboot02.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    List<CommentVo> selectCommentList(@Param("offset")Integer offset,@Param("size")Integer size,@Param("shopId") Long shopId,@Param("cid")  Long cid, @Param("MingRating") Float MingRating,@Param("haveImg")  Boolean haveImg);
    Integer selectCommentListCount(@Param("shopId") Long shopId,@Param("cid")  Long cid, @Param("MingRating") Float MingRating,@Param("haveImg")  Boolean haveImg);

    List<CommentShopVo> selectShopCommentList(@Param("offset")Integer offset,@Param("size")Integer size,@Param("shopId") Long shopId,@Param("cid")  Long cid, @Param("MingRating") Float MingRating,@Param("haveImg")  Boolean haveImg,@Param("haveReply") Boolean haveReply);
    Integer selectShopCommentListCount(@Param("shopId") Long shopId,@Param("cid")  Long cid, @Param("MingRating") Float MingRating,@Param("haveImg")  Boolean haveImg,@Param("haveReply") Boolean haveReply);
}
