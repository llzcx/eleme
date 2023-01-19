package com.cx.springboot02.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.data.vo.SkuVo;
import com.cx.springboot02.dto.AddSkuDto;
import com.cx.springboot02.mapper.BusinessMapper;
import com.cx.springboot02.mapper.CategoryMapper;
import com.cx.springboot02.mapper.FoodMapper;
import com.cx.springboot02.mapper.GoodsMapper;
import com.cx.springboot02.pojo.Business;
import com.cx.springboot02.pojo.Category;
import com.cx.springboot02.pojo.Food;
import com.cx.springboot02.pojo.Goods;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class FoodServiceImpl  extends ServiceImpl<FoodMapper, Food> implements IService<Food> {
    @Autowired
    FoodMapper foodMapper;

    @Autowired
    BusinessMapper businessMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    GoodsMapper goodsMapper;


    @Autowired
    SpecsServiceImpl specsService;


    /**
     * 添加sku
     * @param addSkuDto
     * @return
     */
    public boolean addSku(AddSkuDto addSkuDto){
        Food food = new Food();
        food.setGoodsId(addSkuDto.getGoodsId());
        food.setPrice(addSkuDto.getPrice());
        food.setPackingFee(addSkuDto.getPackingFee());
        food.setSpecsList(addSkuDto.getSpecsList());
        food.setStock(addSkuDto.getStock());
        foodMapper.insert(food);
        return true;
    }




    /**
     * 删除sku
     * @param id
     * @return
     */
    public boolean deleteSku(Long id){
        foodMapper.deleteById(id);
        return true;
    }


    /**
     * 根据spuId获取到对应的sku列表
     * @param supId
     * @return
     */
    public List<Food>  getSkuListBySpuId(Long supId){
        Map<String,Object> mp = new HashMap<>();
        mp.put("goods_id", supId);
        return foodMapper.selectByMap(mp);
    }


    /**
     * 获取对应的sku详细信息
     * @param skuId
     * @return
     */
    public SkuVo getSkuVo(Long skuId){
        Food food = foodMapper.selectBySkuId(skuId);
        if(food==null) return  null;
        Goods goods = goodsMapper.selectById(food.getGoodsId());
        if(goods==null) return null;
        Category category = categoryMapper.selectById(goods.getCategoryId());
        if(category==null) return null;
        Business business = businessMapper.selectById(category.getBusinessId());
        if(business==null) return  null;
        SkuVo skuVo = new SkuVo();
        skuVo.setSkuId(skuId);
        skuVo.setName(goods.getName());
        skuVo.setPackingFee(food.getPackingFee());
        skuVo.setCategoryId(category.getId());
        skuVo.setPrice(food.getPrice());
        skuVo.setSpuId(goods.getId());
        skuVo.setSpecsList(food.getSpecsList());
        skuVo.setShopId(category.getBusinessId());
        skuVo.setSpecsListStr(specsService.getStrByIds(skuVo.getSpecsList()));
        return skuVo;
    }


}
