package com.cx.springboot02.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.pojo.CommentImg;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentImgMapper extends BaseMapper<CommentImg> {

}

