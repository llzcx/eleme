package com.cx.springboot02.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.mapper.SpecsMapper;
import com.cx.springboot02.pojo.Specs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 陈翔
 * @since 2022-11-07
 */
@Service
@Slf4j
public class SpecsServiceImpl extends ServiceImpl<SpecsMapper, Specs> {


    @Autowired
    SpecsMapper specsMapper;

    public String getStrByIds(String specsList){
        if(specsList==null){
            return  "无规格商品";
        }
        StringBuilder sb = new StringBuilder("");
        String[] split = specsList.split("#");
        for (String s : split) {
            if("".equals(s))continue;;
            Long specsId = Long.valueOf(s);
            Specs specs = specsMapper.selectById(specsId);
            sb.append(specs.getName()).append(",");
        }
        return  sb.substring(0, sb.length()-1);
    }
}
