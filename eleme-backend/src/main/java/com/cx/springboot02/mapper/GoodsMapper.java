package com.cx.springboot02.mapper;

import com.cx.springboot02.data.vo.ElasticSearchSpuVo;
import com.cx.springboot02.data.vo.SpuDetailVo;
import com.cx.springboot02.pojo.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 陈翔
 * @since 2022-10-05
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    List<Goods> selectGoodsByCId(@Param("cid") Long cid);
    List<SpuDetailVo> selectSpuList(@Param("offset")Integer offset, @Param("pagesize")Integer pagesize, @Param("key")String key, @Param("categoryId")Long categoryId, @Param("id")Long id, @Param("shopId")Long shopId);
    Integer selectSpuListCount(@Param("key")String key, @Param("categoryId")Long categoryId,@Param("id")Long id,@Param("shopId")Long shopId);

    void insertGoods(Goods goods);

    void replaceOneSkuId(@Param("str")String str,@Param("goods_id")Long goods_id);

    List<ElasticSearchSpuVo> selectAllIdAndName();

}
