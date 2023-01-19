package com.cx.springboot02.controller;


import com.cx.springboot02.common.E.DeliveryWay;
import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.utils.CommonBeanUtils;
import com.cx.springboot02.common.utils.Unobstructed;
import com.cx.springboot02.data.vo.SortWay;
import com.cx.springboot02.dto.AddCategoryDto;
import com.cx.springboot02.pojo.Category;
import com.cx.springboot02.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 陈翔
 * @since 2022-11-04
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
   CategoryServiceImpl categoryService;





    /**
     * 获取一个商家的所有分类
     * @param bid 商家id
     * @return
     */
    @GetMapping("/")
    @Unobstructed
    public JsonResult<List<Category>> getCategories(@PathParam("bid")Long bid){
        System.out.println("bid:"+bid);
        List<Category>  list = categoryService.getCategoryByBId(bid);
        return ResultTool.success(list);
    }


    /**
     * 添加一个商家的所有分类
     * @param addCategoryDto 商家id
     * @return
     */
    @PostMapping("/")
    @Unobstructed
    public JsonResult<Object> add(@RequestBody AddCategoryDto addCategoryDto){
        Category category = new Category();
        CommonBeanUtils.copyProperties(addCategoryDto,category);
        categoryService.save(category);
        return ResultTool.success(null);
    }


    /**
     * 删除商家食品分类业务
     * @param mp
     * @return
     */
    @DeleteMapping("/")
    @Unobstructed
    public JsonResult<Object> delete(@RequestBody Map<String,Object> mp){
        Long categoryId = Long.valueOf(((Integer)mp.get("categoryId")));
        categoryService.removeById(categoryId);
        return ResultTool.success(null);
    }


    /**
     * 获取排序方式
     * @return
     */
    @GetMapping("/sortWay")
    public JsonResult<Object> sortWay(){
        List<SortWay> sortWays = new ArrayList<>();
        for (DeliveryWay value : DeliveryWay.values()) {
            SortWay sortWay = new SortWay();
            sortWay.setSortWayCode(value.getCode());
            sortWay.setSortWayMsg(value.getMessage());
        }
        return ResultTool.success(sortWays);
    }




}

