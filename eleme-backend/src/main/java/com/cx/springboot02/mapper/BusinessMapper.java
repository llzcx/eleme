package com.cx.springboot02.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cx.springboot02.data.vo.ElasticSearchShopVo;
import com.cx.springboot02.data.vo.ShopDetailVo;
import com.cx.springboot02.dto.ShopDto;
import com.cx.springboot02.pojo.Business;
import com.cx.springboot02.pojo.Customer;
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
public interface BusinessMapper extends BaseMapper<Business> {
    Business checkBusiness(@Param("name") String name, @Param("password")String password);

    Business selectBusinessById(@Param("id") Long id);

    ShopDto selectShopDtoByES(@Param("id")Long id,@Param("shopclassid")Integer shopclassid,@Param("sortWay")Integer sortWay,
                              @Param("fengniao")Boolean fengniao,@Param("ping")Boolean ping,@Param("bao")Boolean bao,@Param("zhun")Boolean zhun,
                              @Param("xin")Boolean xin,@Param("online")Boolean online,@Param("piao")Boolean piao);
    /**
     * 商家列表查询 [客户端] 搜索
     * @param offset
     * @param pagesize
     * @param latitude
     * @param longitude
     * @param sid
     * @param key
     * @param sortWay
     * @param fengniao
     * @param ping
     * @param bao
     * @param zhun
     * @param xin
     * @param online
     * @param piao
     * @return
     */
    List<ShopDto> selectBusinessListByDistance(@Param("offset")Integer offset, @Param("pagesize")Integer pagesize,@Param("longitude")Double longitude, @Param("latitude") Double latitude ,@Param("sid")Long sid,@Param("key")String key
    ,@Param("shopclassid")Integer shopclassid,@Param("sortWay")Integer sortWay,@Param("fengniao")Boolean fengniao,@Param("ping")Boolean ping,@Param("bao")Boolean bao,@Param("zhun")Boolean zhun,
                                               @Param("xin")Boolean xin,@Param("online")Boolean online,@Param("piao")Boolean piao);


    List<ShopDetailVo> selectBusinessList(@Param("offset")Integer offset, @Param("pagesize")Integer pagesize, @Param("key")String key, @Param("classId")Long classId, @Param("checkPass")Integer checkPass);
    Integer selectBusinessListCount(@Param("key")String key,@Param("classId")Long classId,@Param("checkPass")Integer checkPass);

    List<Business> selectCheckStateShopList(Integer offset,Integer size,Integer state);

    List<Business> selectALl(Integer offset,Integer size);

    List<ElasticSearchShopVo> selectAllIdAndName();
}
