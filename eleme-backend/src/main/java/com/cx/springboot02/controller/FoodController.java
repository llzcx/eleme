package com.cx.springboot02.controller;


import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.utils.Unobstructed;
import com.cx.springboot02.data.vo.SkuVo;
import com.cx.springboot02.dto.AddSkuDto;
import com.cx.springboot02.pojo.Food;
import com.cx.springboot02.service.impl.FoodServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/food")
@Slf4j
public class FoodController {

    @Autowired
    FoodServiceImpl foodService;

    /**
     * 添加sku
     * @param addSkuDto
     * @return
     */
    @PostMapping("")
    @Unobstructed
    public JsonResult<Object> add(@RequestBody AddSkuDto addSkuDto){
        foodService.addSku(addSkuDto);
        return ResultTool.success(null);
    }


    /**
     * 删除sku
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Unobstructed
    public JsonResult<Object> delete(@PathVariable String id){
        foodService.deleteSku(Long.valueOf(id));
        return ResultTool.success(null);
    }



    /**
     * 更新非规格商品
     * @param addSkuDto
     * @return
     */
    @PutMapping("/single/")
    @Unobstructed
    public JsonResult<Object> updateSingle(@RequestBody AddSkuDto addSkuDto){
        foodService.addSku(addSkuDto);
        return ResultTool.success(null);
    }


    /**
     * 获取sku列表
     * @param spuId
     * @return
     */
    @GetMapping("/{spuId}")
    @Unobstructed
    public JsonResult<Object> getSkuList(@PathVariable String spuId){
        Long id = Long.valueOf(spuId);
        List<Food> skuListBySpuId = foodService.getSkuListBySpuId(id);
        return ResultTool.success(skuListBySpuId);
    }



    /**
     * 通过skuid往上获取一些信息 [主要用于添加购物车]
     * @param skuId
     * @return
     */
    @GetMapping("/goodsInfo/{skuId}")
    @Unobstructed
    public JsonResult<SkuVo> getGoodsInfo(@PathVariable String skuId){
        SkuVo skuVo = foodService.getSkuVo(Long.valueOf(skuId));
        return ResultTool.success(skuVo);
    }
}
