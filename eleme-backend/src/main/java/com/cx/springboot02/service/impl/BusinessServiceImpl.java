package com.cx.springboot02.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.common.E.ShopCheckNum;
import com.cx.springboot02.common.RPage;
import com.cx.springboot02.common.elasticsearch.ElasticSearchIKWord;
import com.cx.springboot02.common.elasticsearch.ShopListSearchDao;
import com.cx.springboot02.common.redis.HotKeyCache.HotKeyCache;
import com.cx.springboot02.common.redis.HotKeyCache.ZSetData;
import com.cx.springboot02.common.redis.RedisOperator;
import com.cx.springboot02.common.redis.shopCache.BusinessRedisService;
import com.cx.springboot02.common.utils.CommonBeanUtils;
import com.cx.springboot02.common.utils.DataUtils;
import com.cx.springboot02.common.utils.PositionUtil;
import com.cx.springboot02.common.utils.StringUtil;
import com.cx.springboot02.data.vo.ElasticSearchShopVo;
import com.cx.springboot02.data.vo.ShopDetailVo;
import com.cx.springboot02.dto.AddShopDto;
import com.cx.springboot02.dto.ShopDto;
import com.cx.springboot02.mapper.BusinessMapper;
import com.cx.springboot02.pojo.Business;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.geo.GeoUtils;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cx.springboot02.common.utils.Final.*;
import static com.cx.springboot02.common.utils.Final.USERINFO_LIVE_TIME;


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
public class BusinessServiceImpl extends ServiceImpl<BusinessMapper, Business> {

    @Autowired
    BusinessMapper businessMapper;

    @Autowired
    RestHighLevelClient client;

    @Autowired
    ShopListSearchDao shopListSearchDao;

    @Autowired
    RedisOperator redisOperator;

    @Autowired
    BusinessRedisService businessRedisService;

    @Autowired
    HotKeyCache hotKeyCache;





    public Business selectBusinessById(Long id) {
        return businessMapper.selectBusinessById(id);
    }


    /**
     * 拦截器认证
     * @return
     */
    public Boolean login(String token,String name,String password){
        String redisKey = Un(REDIS_ELEME,REDIS_SAVE_LOGIN_INFO,BUSINESS_TOKEN,token);
        try {
            Business business1 = redisOperator.getObject(redisKey,Business.class);
            //商铺通过审核 且 没有被停用
            if(business1 != null && business1.getCheckPass().equals(1) && !business1.getDeactivate()) {
                if(name.equals(business1.getName()) && password.equals(business1.getPassword())){
                    //刷新redis存在时间
                    redisOperator.expire(redisKey, USERINFO_LIVE_TIME);
                    //在redis当中有存储
                    return true;
                }else{
                    return false;
                }
            } else{
                //redis不存在直接返回false
                return false;
            }
        } catch (ClassCastException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public Business getBusinessByAAndP(String name,String password){
        return businessMapper.checkBusiness(name,password);
    }


    /**
     * 分页获取商家
     *
     * @param offset
     * @param size
     * @return
     */
    public RPage<Business> getBusinessListPage(int offset, int size) {
        LambdaQueryWrapper<Business> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Business> userPage = new Page<>(offset, size);
        IPage<Business> businessIPage = businessMapper.selectPage(userPage, userLambdaQueryWrapper);
        return new RPage<Business>(businessIPage);
    }


    public RPage<ShopDto> getBusinessByDistance(int pagenum, int size, Double longitude, Double latitude, Long sid, String key
            , Integer shopclassid, Integer sortWay, Boolean fengniao, Boolean ping, Boolean bao, Boolean zhun, Boolean xin, Boolean online, Boolean piao) {
        return new RPage<>(pagenum, size, businessMapper.selectBusinessListByDistance(size * (pagenum - 1), size, longitude, latitude, sid, key,
                shopclassid, sortWay, fengniao, ping, bao, zhun, xin, online, piao));
    }

    /***
     * 在es当中查询用户端商家列表
     * @param lastId
     * @param size
     * @param longitude
     * @param latitude
     * @param sid
     * @param key
     * @param shopclassid
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
    public RPage<ShopDto> getBusinessByEs(String lastId, Integer size, Double longitude, Double latitude, Long sid, String key
            , Integer shopclassid, Integer sortWay, Boolean fengniao, Boolean ping, Boolean bao, Boolean zhun, Boolean xin, Boolean online, Boolean piao) throws Exception {
        SearchHit[] hits = shopListSearchDao.selectShopList(lastId, size, longitude, latitude, key);
        List<ShopDto> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            id = id.substring(PREFIX_SHOP_ID.length());
            DocumentField distance_in_meters = hit.getFields().get("distance_in_meters");
            List<Object> values = distance_in_meters.getValues();
            ShopDto shopDto = new ShopDto();
            Business shopRedis = businessRedisService.getShopById(Long.valueOf(id));
            if(shopRedis!=null){
                CommonBeanUtils.copyProperties(shopRedis, shopDto);
            }else{
                shopDto = businessMapper.selectShopDtoByES(Long.valueOf(id), shopclassid, sortWay, fengniao, ping, bao, zhun, xin, online, piao);
                if(shopDto!=null){
                    Business temp = new Business();
                    CommonBeanUtils.copyProperties(shopDto, temp);
                    businessRedisService.saveShop(temp);
                }
            }
            if(shopDto!=null){
                //防止这个商店被关闭或者审核没有通过
                Double distance =(Double) values.get(0);
                distance = DataUtils.PrecisionControl(distance,2);
                shopDto.setDistance(distance);
                list.add(shopDto);
            }
        }
        //对列表进行过滤
        List<ShopDto> shopDtos = checkList(list,shopclassid, sortWay, fengniao, ping, bao, zhun, xin, online, piao);
        list = shopDtos;
        //对key进行解析
        if(key!=null &&!"".equals(key)){
            //保存
            hotKeyCache.SaveHotWord(key, PositionUtil.ToGeohash(longitude, latitude));
        }

        String thisLastId = null;
        //如果存在多个就保存最后一个id 下
        if(hits.length>0){
            thisLastId = hits[hits.length-1].getId();
        }
        log.info("下次分页需携带:"+thisLastId);
        return new RPage<>(thisLastId, size, list);
    }

    /**
     * 对列表进行筛选
     * @param shopDtos
     * @param shopclassid
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
    public List<ShopDto> checkList(List<ShopDto> shopDtos, Integer shopclassid, Integer sortWay,
                          Boolean fengniao,Boolean ping, Boolean bao,Boolean zhun,
                          Boolean xin, Boolean online, Boolean piao){
        List<ShopDto> newList = new ArrayList<>();

        for (ShopDto shopDto : shopDtos) {
            if(shopclassid!=null && !shopDto.getShopClassId().equals(Long.valueOf(shopclassid))){
                continue;
            }
            if(fengniao!=null && !shopDto.getDeliveryMode().equals(fengniao)){
                continue;
            }
            if(ping!=null && !shopDto.getIsPremium().equals(ping)){
                continue;
            }
            if(bao!=null && !shopDto.getBao().equals(bao)){
                continue;
            }
            if(zhun!=null && !shopDto.getZhun().equals(zhun)){
                continue;
            }
            if(xin!=null && !shopDto.getNewShop().equals(xin)){
                continue;
            }
            if(piao!=null && !shopDto.getPiao().equals(piao)){
                continue;
            }
            newList.add(shopDto);
        }
        return newList;
    }

    public List<ZSetData> getKeySortList(String geohash){
        return hotKeyCache.getHotWordList(geohash);
    }


    public static void main(String[] args) {
        String abc = "abc";
        String id = "123";
        String abcid = abc +id;
        abcid = abcid.substring(abc.length());
        System.out.println(abcid);
    }

    /**
     * 获取商家列表
     *
     * @param pagenum
     * @param size
     * @param key
     * @param classId
     * @param checkPass
     * @return
     */
    public RPage<ShopDetailVo> getBusinessList(int pagenum, int size, String key, Long classId, Integer checkPass) {
        RPage<ShopDetailVo> rPage = new RPage<>(pagenum, size, businessMapper.selectBusinessList(size * (pagenum - 1), size, key, classId, checkPass));
        for (ShopDetailVo row : rPage.getRows()) {
            /**
             * 设置评分
             */
            row.setRating(5.0f);
            /**
             * 设置销量
             */
            row.setRecentOrderNum(463);
            /**
             * 是否在学校当中
             */
            row.setInSchool(false);

            /**
             * 设置检查状态的字符串形式
             */
            row.setStateStr(ShopCheckNum.getMessageByCode(row.getCheckPass()));
        }
        rPage.SetTotalCountAndTotalPage(businessMapper.selectBusinessListCount(key, classId, checkPass));
        return rPage;
    }

    public void saveShop(AddShopDto addShopDto) {
        Business business = new Business();
        business.setId(addShopDto.getShopId());
        business.setAddress(addShopDto.getAddress());
        business.setPhone(addShopDto.getPhone());
        business.setLatitude(addShopDto.getLatitude());
        business.setLongitude(addShopDto.getLongitude());
        business.setCategory(addShopDto.getCategory());
        business.setImagePath(addShopDto.getImagePath());
        business.setFloatDeliveryFee(addShopDto.getFloatDeliveryFee());
        business.setFloatMinimumOrderAmount(addShopDto.getFloatMinimumOrderAmount());
        business.setDescription(addShopDto.getDescription());
        business.setPromotionInfo(addShopDto.getPromotionInfo());
        business.setIsPremium(addShopDto.getIsPremium());
        business.setDeliveryMode(addShopDto.getDeliveryMode());
        business.setNewShop(addShopDto.getNewShop());
        business.setBao(addShopDto.getBao());
        business.setPiao(addShopDto.getPiao());
        business.setStartTime(addShopDto.getStartTime());
        business.setEndTime(addShopDto.getEndTime());
        business.setBusinessLicenseImage(addShopDto.getBusinessLicenseImage());
        business.setCateringServiceLicenseImage(addShopDto.getCateringServiceLicenseImage());
        businessMapper.updateById(business);


        ElasticSearchShopVo shop = new ElasticSearchShopVo();
        shop.setName(addShopDto.getName());
        shop.setId(addShopDto.getShopId());
        shop.setLatitude(addShopDto.getLatitude());
        shop.setLongitude(addShopDto.getLongitude());
        shopListSearchDao.addShop(shop);
    }

    /**
     * 改变商家审核状态
     *
     * @param bid
     * @param shopCheckNum
     * @return
     */
    public boolean alterCheckState(Long bid, ShopCheckNum shopCheckNum) {
        Business business = new Business();
        business.setId(bid);
        business.setCheckPass(shopCheckNum.getCode());
        businessMapper.updateById(business);
        return true;
    }

    /**
     * 获取不同审核状态的商家列表(分页)
     *
     * @return
     */
    public RPage<Business> getCheckStateList(int pagenum, int size, ShopCheckNum shopCheckNum) {
        Map<String, Object> mp = new HashMap<>();
        return new RPage<>(pagenum, size, businessMapper.selectCheckStateShopList(size * (pagenum - 1), size, shopCheckNum.getCode()));
    }


    /**
     * 获取所有的商家列表(分页)
     *
     * @return
     */
    public RPage<Business> getAll(int pagenum, int size) {
        Map<String, Object> mp = new HashMap<>();
        return new RPage<>(pagenum, size, businessMapper.selectALl(size * (pagenum - 1), size));
    }


}
