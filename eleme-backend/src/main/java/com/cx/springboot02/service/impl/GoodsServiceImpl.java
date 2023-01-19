package com.cx.springboot02.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.common.RPage;
import com.cx.springboot02.common.elasticsearch.ShopListSearchDao;
import com.cx.springboot02.common.utils.CommonBeanUtils;
import com.cx.springboot02.data.vo.*;
import com.cx.springboot02.dto.*;
import com.cx.springboot02.mapper.*;
import com.cx.springboot02.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 陈翔
 * @since 2022-10-05
 */
@Service
@Slf4j
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IService<Goods> {

    @Autowired
    GoodsMapper goodsMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    Specs_categoryMapper specs_categoryMapper;

    @Autowired
    SpecsMapper specsMapper;

    @Autowired
    FoodMapper foodMapper;

   @Autowired
   ShopListSearchDao shopListSearchDao;


    /**
     * 通过分类id获取所有的商品
     *
     * @param id
     */
    public List<Goods> getGoodsByCid(Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("category_id", id);
        return goodsMapper.selectByMap(map);
    }


    /**
     * 根据商家id获取菜单列表
     * @param bid
     * @return
     */
    public List<MenuDto> get(Long bid) {
        List<MenuDto> menuDtos = new ArrayList<>();
        List<Category> categories = categoryMapper.selectCategoriesById(bid);
        //第一层获取商家分类
        for (Category category : categories) {
            MenuDto menuDto = new MenuDto();
            menuDto.setCategoryId(category.getId());
            menuDto.setBusinessId(bid);
            menuDto.setCategoryId(category.getId());
            menuDto.setName(category.getName());
            menuDto.setDescription(category.getDescription());
            List<FoodDto> foodDtos = new ArrayList<>();
            //根据categoryId获取对应的spu
            List<Goods> goodsList = goodsMapper.selectGoodsByCId(category.getId());
            //第二层获取spu列表
            for (Goods goods : goodsList) {
                FoodDto foodDto = new FoodDto();
                foodDto.setCategoryId(goods.getCategoryId());
                foodDto.setIsSingle(goods.getIsSingle());
                foodDto.setDescription(goods.getDescription());
                foodDto.setImagePath(goods.getImagePath());
                foodDto.setName(goods.getName());
                foodDto.setMinSpecsCount(goods.getMinSpecsCount());
//                    foodDto.setSatisfyRate();
//                    foodDto.setActivity();
//                    foodDto.setMonthSales();
                foodDto.setSpuId(goods.getId());
                List<SpecsCategoryDto> specsCategoryDtos = new ArrayList<>();
                if(!goods.getIsSingle()){
                    List<SpecsCategory> specsCategories = specs_categoryMapper.selectByGoodsId(goods.getId());
                    //第三层获取规格分类列表
                    for (SpecsCategory specsCategory : specsCategories) {
                        SpecsCategoryDto specsCategoryDto = new SpecsCategoryDto();
                        specsCategoryDto.setId(specsCategory.getId());
                        specsCategoryDto.setName(specsCategory.getName());
                        specsCategoryDto.setSort(specsCategory.getSort());
                        List<Specs> specsList = specsMapper.selectAllBySpecsCategoryId_Single(specsCategory.getId());
                        //获取规格 设置规格
                        specsCategoryDto.setSpecsList(specsList);
                        specsCategoryDtos.add(specsCategoryDto);
                    }
                }else{
                    //没有规格列表
                }
                foodDto.setSpecs(specsCategoryDtos);
                foodDtos.add(foodDto);
                List<Food> foodList;
                //第三层获取食物
                if(!goods.getIsSingle()) {
                    //获取规格sku列表
                    foodList = foodMapper.selectFoodListByGId_Not_Single(goods.getId());
                }else{
                    //获取非规格商品sku
                    foodList = foodMapper.selectFoodListByGId_Single(goods.getId());
                }
                foodDto.setFoods(foodList);
            }
            menuDto.setFoodDto(foodDtos);
            menuDtos.add(menuDto);
        }
        return menuDtos;
    }

    /**
     * 添加spu
     */

    public boolean addSpu(AddSpuDto addSpuDto){
        Goods goods = new Goods();
        CommonBeanUtils.copyProperties(addSpuDto, goods);
        goodsMapper.insert(goods);
        if(goods.getIsSingle()){
            //单规格商品 添加一个sku
            Food food = new Food();
            //复制属性
            CommonBeanUtils.copyProperties(addSpuDto, food);
            food.setGoodsId(goods.getId());
            if(foodMapper.insert(food)!=0){
                System.out.println("插入sku成功!");
            }
        }else{
            //多规格商品
        }


        ElasticSearchSpuVo spu = new ElasticSearchSpuVo();
        spu.setName(addSpuDto.getName());
        spu.setId(goods.getId());
        //根据分类id找到商家
        Category category = categoryMapper.selectById(goods.getCategoryId());
        spu.setBusinessId(category.getBusinessId());
        shopListSearchDao.addSpu(spu);
        return true;
    }


    /**
     * 删除spu
     */
    @Transactional
    public boolean deleteSpu(Long id){
        //先删除spu 然后删除sku
        Goods goods = new Goods();
        goods.setId(id);
        goodsMapper.deleteById(goods);
        Map<String,Object> mp = new HashMap<>();
        mp.put("goods_id", id);
        foodMapper.deleteByMap(mp);
        //删除这个商品的所有规格和规格分类
        Map<String,Object> mp1 = new HashMap<>();
        mp1.put("goods_id", goods.getId());
        List<SpecsCategory> specsCategories = specs_categoryMapper.selectByMap(mp1);
        specs_categoryMapper.deleteByMap(mp1);
        Map<String,Object> mp2 = new HashMap<>();
        for (SpecsCategory specsCategory : specsCategories) {
            mp2.clear();
            mp2.put("specs_category_id", specsCategory.getId());
            specsMapper.deleteByMap(mp2);
        }
        //在es中移除
        shopListSearchDao.deleteShop(id);
        return true;
    }

    /**
     * 获取分页的spu列表
     * @param pagenum
     * @param size
     * @param key
     * @param categoryId
     * @param id
     * @param shopId
     * @return
     */
    public  RPage<SpuDetailVo> getSpuList(int pagenum, int size, String key, Long categoryId, Long id, Long shopId){
        log.info("goodsMapper{} ",goodsMapper);
        RPage<SpuDetailVo> rPage = new RPage<>(pagenum, size, goodsMapper.selectSpuList(size * (pagenum - 1), size, key, categoryId, id,shopId));
        for (SpuDetailVo row : rPage.getRows()) {
            row.setOnShelvesStr(row.getOnShelves() ?"已上架":"未上架");
            row.setDeleteState(row.getIsDelete() ?"已删除":"未删除");
            row.setRating(4.9F);
            row.setMonthSales(48);

            /******设置价格和打包费 当为单价商品的时候*****/
            Map<String,Object> mp = new HashMap<>();
            mp.put("goods_id", row.getId());
            List<Food> foodList = foodMapper.selectByMap(mp);
            Float singlePrice = null;
            Float SinglePackingFee = null;
            if(foodList.size()>0){
                singlePrice = foodList.get(0).getPrice();
                SinglePackingFee = foodList.get(0).getPackingFee();
            }
            Float price = row.getIsSingle()?singlePrice:null;
            Float packingFee =row.getIsSingle()?SinglePackingFee:null;
            row.setPrice(price);
            row.setPackingFee(packingFee);
            /*****设置价格******/

            List<SkuDetailVo> list = foodMapper.selectSkuDetailVoList(row.getId());
            for (SkuDetailVo skuDetailVo : list) {
                //创建存储集合
                List<String> skuStrs = new ArrayList<>();
                //拿到id列表 当id列表不为空时
                if(skuDetailVo.getSpecsList() != null && !"".equals(skuDetailVo.getSpecsList() )){
                    String[] skuIds = skuDetailVo.getSpecsList().split("#");
                    for (String skuId : skuIds) {
                        if(skuId==null || "".equals(skuId)) continue;
                        Long idL = Long.valueOf(skuId);
                        //利用id去查找这个规格
                        Specs specs = specsMapper.selectById(idL);
                        skuStrs.add(specs.getName());
                    }
                }
                skuDetailVo.setSpecsValue(skuStrs);
            }
            row.setSkuList(list);
        }
        rPage.SetTotalCountAndTotalPage(goodsMapper.selectSpuListCount(key, categoryId, id, shopId));
        return rPage;
    }

    /**
     * 更新单价商品
     * @param updateSpuSingleDto
     */
    public void updateSingleSpu(UpdateSpuSingleDto updateSpuSingleDto){
        //创建对象
        Goods goods = new Goods();
        goods.setId(updateSpuSingleDto.getGoodsId());
        CommonBeanUtils.copyProperties(updateSpuSingleDto, goods);
        goodsMapper.updateById(goods);
        //找到对应的sku 将sku的打包费 价格更新
        //每个单价的商品只有一个对应的sku 这个sku就是我们需要去更新的一个sku
        Food food = foodMapper.selectSkuBySingleSpuId(updateSpuSingleDto.getGoodsId());
        food.setPrice(updateSpuSingleDto.getPrice());
        food.setPackingFee(updateSpuSingleDto.getPackingFee());
        foodMapper.updateById(food);
        //es同步
        Goods goods1 = goodsMapper.selectById(updateSpuSingleDto.getGoodsId());
        shopListSearchDao.updateSpuData(goods1.getId(), goods1.getName());
    }

    /**
     * 更新规格商品
     * @param updateSpuNotSingleDto
     */
    public void updateNotSingleSpu(UpdateSpuNotSingleDto updateSpuNotSingleDto){
        //创建对象
        Goods goods = new Goods();
        //把spu需要更新的字段设置一下
        goods.setId(updateSpuNotSingleDto.getGoodsId());
        CommonBeanUtils.copyProperties(updateSpuNotSingleDto, goods);
        goodsMapper.updateById(goods);
        //es同步
        Goods goods1 = goodsMapper.selectById(updateSpuNotSingleDto.getGoodsId());
        shopListSearchDao.updateSpuData(goods1.getId(), goods1.getName());
    }

    public static void main(String[] args) {
        String ids = "";
        String[] idss =ids.split("#");
        for (String s : idss) {
            System.out.println(s+ " ");
        }
    }


}
