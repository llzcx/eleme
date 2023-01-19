package com.cx.springboot02.controller;


import com.cx.springboot02.common.E.ResultCode;
import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.utils.CommonBeanUtils;
import com.cx.springboot02.common.utils.Unobstructed;
import com.cx.springboot02.data.vo.SpecsCategoryVo;
import com.cx.springboot02.dto.AddSpecsCategoryDto;
import com.cx.springboot02.dto.AddSpecsDto;
import com.cx.springboot02.mapper.FoodMapper;
import com.cx.springboot02.mapper.GoodsMapper;
import com.cx.springboot02.mapper.SpecsMapper;
import com.cx.springboot02.mapper.Specs_categoryMapper;
import com.cx.springboot02.pojo.Specs;
import com.cx.springboot02.pojo.SpecsCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specs")
@Slf4j
public class SpecsController {


    @Autowired
    SpecsMapper specsMapper;

    @Autowired
    Specs_categoryMapper specsCategoryMapper;

    @Autowired
    FoodMapper foodMapper;

    @Autowired
    GoodsMapper goodsMapper;

    /**
     * 添加规格分类 [添加规格分类会导致规格商品的specsList结构出现差错 这里将food的specsList增加一个规格:添加规格分类时添加的规格]
     *
     * @return
     */
    @PostMapping("/addCategories")
    @Unobstructed
    public JsonResult getCategories(@RequestBody AddSpecsCategoryDto addSpecsCategoryDto) {
        SpecsCategory specsCategory = new SpecsCategory();
        CommonBeanUtils.copyProperties(addSpecsCategoryDto, specsCategory);
        specsCategory.setGoodsId(addSpecsCategoryDto.getGoodId());
        specsCategoryMapper.insert(specsCategory);
        Specs specs = new Specs();
        specs.setSpecsCategoryId(specsCategory.getId());
        specs.setDescription(addSpecsCategoryDto.getSpecsDescription());
        specs.setName(addSpecsCategoryDto.getSpecsName());
        specsMapper.insert(specs);
        //利用 mybatisplus的特性这里插入以后会直接给到插入后的主键
        //1.先拿到规格分类上面的spuId
        Long spuId = specsCategory.getGoodsId();
        //2.拿到id以后去更新和这个id下面的所有sku 将这些sku的specslist去增加一个id#
        foodMapper.updateSkuList(specs.getId().toString()+"#",spuId);
        return ResultTool.success(null);
    }

    /**
     * 添加规格 [添加一个规格不会对现有的数据造成影响 直接添加即可]
     *
     * @return
     */
    @PostMapping("/addSpecs")
    @Unobstructed
    public JsonResult getCategories(@RequestBody AddSpecsDto addSpecsDto) {
        Specs specs = new Specs();
        specs.setName(addSpecsDto.getName());
        specs.setDescription(addSpecsDto.getDescription());
        specs.setSpecsCategoryId(addSpecsDto.getSpecsCategoryId());
        specsMapper.insert(specs);
        return ResultTool.success(null);
    }


    /**
     * 获取一个商品[spu]的所有规格分类
     *
     * @return
     */
    @GetMapping("/list/{id}")
    @Unobstructed
    public JsonResult getAllCategories(@PathVariable String id) {
        Long gid = Long.valueOf(id);
        Map<String,Object> mp = new HashMap<>();
        mp.put("goods_id",gid);
        List<SpecsCategory> specsCategories = specsCategoryMapper.selectByMap(mp);
        return ResultTool.success(specsCategories);
    }

    /**
     * 获取一个商品[spu]的所有规格分类和其下的所有规格 方便前端形成树状结构的选择栏
     *
     * @return
     */
    @GetMapping("/categoryAndSpecsList/{id}")
    @Unobstructed
    public JsonResult categoryAndSpecsList(@PathVariable String id) {
        Long gid = Long.valueOf(id);
        List<SpecsCategoryVo> specsCategoryVos = specsCategoryMapper.selectSpecsListVo(gid);
        for (SpecsCategoryVo specsCategory : specsCategoryVos) {
            Map<String,Object> mp =new HashMap<>();
            mp.put("specs_category_id",specsCategory.getId());
            List<Specs> specsList = specsMapper.selectByMap(mp);
            specsCategory.setSpecsList(specsList);
        }
        return ResultTool.success(specsCategoryVos);
    }


    /**
     * 删除一个分类下的规格 [先判断是否能够去删除,最少得留下一个] [删除规格会导致包含这个规格的所有商品都被删除]
     * @return
     */
    @DeleteMapping("/specs/{specsId}")
    @Unobstructed
    public JsonResult deleteSpecs(@PathVariable String specsId) {
        Long specsIdL = Long.valueOf(specsId);
        //先去select到这个规格的规格分类
        Specs specs = specsMapper.selectById(specsIdL);
        if(specs==null){
            return ResultTool.fail(ResultCode.FIELD_IS_EMPTY);
        }
        //通过这个分类的id去找到这个分类下面的所有规格分类
        Integer num = specsMapper.getNumFromACategory(specs.getSpecsCategoryId());
        if(num <= 1){
            //最少得留下一个规格
            return ResultTool.fail(ResultCode.SPECS_MUST_HAVE_ONE);
        }else{
            //删除规格
            specsMapper.deleteById(specsIdL);
            //删除含有此规格的sku | 先找到这个规格分类
            SpecsCategory specsCategory = specsCategoryMapper.selectById(specs.getSpecsCategoryId());
            //根据此规格的分类id找到对应的商品(spuId)
            Long goodId = specsCategory.getGoodsId();//goodId可以缩小搜索范围
//            String str = '#' + specsId +'#'; 可以这样做 但是没有必要 直接利用id的字符串形式然后利用mysql的find_in_set()函数去进行判断删除
            foodMapper.deleteBySpecsId(specsId,goodId);
            return ResultTool.success(null);
        }
    }



    /**
     * 删除一个规格分类
     * @return
     */
    @DeleteMapping("/deleteSpecsCategory/{specsCategoryId}")
    @Unobstructed
    public JsonResult deleteSpecsCategory(@PathVariable String specsCategoryId){
        //先去数据库查看是否含有此规格分类id
        SpecsCategory specsCategory = specsCategoryMapper.selectById(Long.valueOf(specsCategoryId));
        if(specsCategory==null){
            return ResultTool.fail(ResultCode.PARAM_NOT_VALID);
        }else{
            //有这个规格id
            //拿到spuId
            Long goodId = specsCategory.getGoodsId();
            //再利用分类id找到所有的规格
            Map<String,Object> mp = new HashMap<>();
            mp.put("specs_category_id",specsCategory.getId());
            List<Specs> specsList = specsMapper.selectByMap(mp);
            //将对应的sku修改
            for (Specs specs : specsList) {
                //例如: 将 #1#2#3#4#5# 中的 3删除 那就是: #1#2#4#5 => 等价于 将#3# 替换成 #
                String str = '#' + specs.getId().toString() + '#';
                //替换掉
                goodsMapper.replaceOneSkuId("#"+specs.getId()+'#',goodId);
            }
            //然后删除对应子的规格
            Map<String,Object> mp1 = new HashMap<>();
            mp1.put("specs_category_id", specsCategoryId);
            specsMapper.deleteByMap(mp1);
            //最后再删除这个分类
            specsCategoryMapper.deleteById(specsCategory.getId());
            return  ResultTool.success(null);
        }
    }

}

