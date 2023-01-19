package com.cx.springboot02.controller;


import com.cx.springboot02.common.E.AuthorizeType;
import com.cx.springboot02.common.E.ResultCode;
import com.cx.springboot02.common.E.ShopCheckNum;
import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.RPage;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.elasticsearch.ShopListSearchDao;
import com.cx.springboot02.common.redis.HotKeyCache.ZSetData;
import com.cx.springboot02.common.redis.RedisOperator;
import com.cx.springboot02.common.redis.loginCache.LoginCache;
import com.cx.springboot02.common.redis.shopCache.BusinessRedisService;
import com.cx.springboot02.common.utils.*;
import com.cx.springboot02.data.vo.ElasticSearchShopVo;
import com.cx.springboot02.dto.AddShopDto;
import com.cx.springboot02.dto.UpdateCheckPassDto;
import com.cx.springboot02.dto.UpdateDeactivateDto;
import com.cx.springboot02.dto.UpdateShopDto;
import com.cx.springboot02.pojo.Business;
import com.cx.springboot02.service.impl.BusinessServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

import static com.cx.springboot02.common.utils.Final.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 陈翔
 * @since 2022-10-05
 */
@RestController
@RequestMapping("/business")
@Slf4j

public class BusinessController {
    @Autowired
    BusinessServiceImpl businessService;

    @Autowired
    RedisOperator redisOperator;

    @Autowired
    ShopListSearchDao shopListSearchDao;

    @Autowired
    BusinessRedisService businessRedisService;


    @Autowired
    LoginCache loginCache;






    @PostMapping("/login")
    @Unobstructed
    public JsonResult<Object> login(@RequestBody Map<String, Object> mp, HttpServletResponse response){
        String name = (String) mp.get("name");
        String password = (String) mp.get("password");
        Business business = businessService.getBusinessByAAndP(name, password);
        if(name!=null && password!=null && business!=null){
            //删除之前的token
            loginCache.deleteBusiness(business.getId());
            //生成token
            String token = JwtUtils.createToken(name, password,"customer");
            //存入redis
            loginCache.saveBusinessToken(token, business);
            //放入请求头
            response.addHeader("Access-Control-Expose-Headers","token");
            response.addHeader("token",token);
            System.out.println(response.getHeader("token"));
            return ResultTool.success(business);
        }else{
            return ResultTool.fail(ResultCode.USER_CREDENTIALS_ERROR);
        }
    }

    /**
     * 获取商家列表 可有条件:比如sid
     * @param pagenum
     * @param size
     * @return
     */
    @GetMapping("/list")
    @Unobstructed
    public JsonResult<Object> getBusinessList(@PathParam("pagenum")Integer pagenum,@PathParam("size")Integer size
            ,@PathParam("key")String key,@PathParam("ClassId")Long ClassId,@PathParam("checkPass") Integer checkPass){
        log.info("{} {} {}  {}",pagenum,size,key,checkPass);
        RPage rPage = null;
        try {
            rPage = businessService.getBusinessList(pagenum, size,key,ClassId,checkPass);
            return ResultTool.success(rPage);
        } catch (Exception exception) {
            exception.printStackTrace();
            return  ResultTool.fail(ResultCode.PARAM_NOT_COMPLETE);
        }
    }


    public Boolean getBoolValue(String b){
        if(b ==null) return  null;
        return Boolean.valueOf(b);
    }
    public Long getLongValue(String id){
        if(id==null) return null;
        return Long.valueOf(id);
    }
    /**
     *
     * @param pagenum 第几页数据
     * @param size  每页多少条
     * @param key 关键词
     * @param longitude 经度3
     * @param latitude 纬度
     * @param sid 商品类别id
     * @return
     */
    @GetMapping(value = {"/"})
    @Unobstructed
    public JsonResult<Object> getBusinessListByDistance(@PathParam("pagenum")Integer pagenum,@PathParam("size")Integer size,@PathParam("key")String key,
    @PathParam("longitude")Double longitude,@PathParam("latitude") Double latitude,@PathParam("sid") Long sid, @PathParam("sortway")Integer sortWay,
    @PathParam("shopclassid")Integer shopclassid,@PathParam("fengniao")String fengniao,@PathParam("ping")String ping,@PathParam("bao")String bao,
    @PathParam("zhun")String zhun,@PathParam("xin")String xin,@PathParam("onlinepay")String onlinepay,@PathParam("piao")String piao
    ){
        RPage rPage = null;
        log.info("pagenum {} size {} key {} longitude {} latitude {} sid {} sortway {} shopclassid {} fengniao {} ping {} bao {} zhun {} xin {} onlinepay {} piao{} ",
                pagenum,size,key,longitude,latitude,sid,sortWay,shopclassid,fengniao,ping,bao,zhun,xin,onlinepay,piao);
        try {
            rPage = businessService.getBusinessByDistance(pagenum, size,longitude,latitude,sid,key,shopclassid,sortWay,
                    getBoolValue(fengniao), getBoolValue(ping), getBoolValue(bao), getBoolValue(zhun), getBoolValue(xin), getBoolValue(onlinepay), getBoolValue(piao));
            return ResultTool.success(rPage);
        } catch (Exception exception) {
            exception.printStackTrace();
            return  ResultTool.fail(ResultCode.PARAM_NOT_COMPLETE);
        }
    }

    @GetMapping(value = {"/GoodPerformance"})
    @Unobstructed
    public JsonResult<Object> getBusinessListByElasticSearch(@PathParam("lastId")String lastId,@PathParam("size")Integer size,@PathParam("key")String key,
                                                        @PathParam("longitude")Double longitude,@PathParam("latitude") Double latitude,@PathParam("sid") Long sid, @PathParam("sortway")Integer sortWay,
                                                        @PathParam("shopclassid")Integer shopclassid,@PathParam("fengniao")String fengniao,@PathParam("ping")String ping,@PathParam("bao")String bao,
                                                        @PathParam("zhun")String zhun,@PathParam("xin")String xin,@PathParam("onlinepay")String onlinepay,@PathParam("piao")String piao
    ){

        if(latitude==null || longitude==null){
            return  ResultTool.fail(ResultCode.PARAM_NOT_COMPLETE);
        }
        RPage rPage = null;
        log.info("lastId {} ,size {} ,key {} ,longitude {} ,latitude {} ,sid {} ,sortway {} ,shopclassid {} ,fengniao {}, ping {}, bao {} ,zhun {} ,xin {} ,onlinepay {}, piao{} ",
                lastId,size,key,longitude,latitude,sid,sortWay,shopclassid,fengniao,ping,bao,zhun,xin,onlinepay,piao);
        try {
            rPage = businessService.getBusinessByEs(lastId, size,longitude,latitude,sid,key,shopclassid,sortWay,
                    getBoolValue(fengniao), getBoolValue(ping), getBoolValue(bao), getBoolValue(zhun), getBoolValue(xin), getBoolValue(onlinepay), getBoolValue(piao));
            return ResultTool.success(rPage);
        } catch (Exception exception) {
            exception.printStackTrace();
            return  ResultTool.fail(ResultCode.PARAM_NOT_COMPLETE);
        }
    }


    @GetMapping(value = {"/getKeyList"})
    @Unobstructed
    public JsonResult getHotKeyList(@PathParam("geohash")String geohash){
        List<ZSetData> keySortList = businessService.getKeySortList(geohash);
        return ResultTool.success(keySortList);
    }


    /**
     * 通过商家id 获取到对应的商家数据
     * @param id
     * @return
     */
    @GetMapping(value = {"/{id}"})
    @Unobstructed
    public JsonResult<Object> getBusinessListById(@PathVariable String id){
        Business business = businessService.getById(Long.valueOf(id));
        return ResultTool.success(business);
    }


    /**
     * 商家信息补充
     * @param addShopDto
     * @return
     */
    @PostMapping("/add")
    @Unobstructed
    public JsonResult addShop(@RequestBody AddShopDto addShopDto){

        businessService.saveShop(addShopDto);
        return ResultTool.success();
    }


    /**
     * 停用商家
     * @param updateDeactivateDto
     * @return
     */
    @PutMapping("/deactivate")
    @Unobstructed
    public JsonResult updateShopDeactivate(@RequestBody UpdateDeactivateDto updateDeactivateDto){
        Business business = new Business();
        business.setId(updateDeactivateDto.getShopId());
        business.setDeactivate(updateDeactivateDto.getDeactivate());
        //删除redis缓存
        businessRedisService.deleteShopById(business.getId());

        //删除商家登录的凭证
        loginCache.deleteBusiness(updateDeactivateDto.getShopId());

        businessService.updateById(business);
        return ResultTool.success();
    }

    /**
     * 申请通过/不通过商家
     * @param updateCheckPassDto
     * @return
     */
    @PutMapping("/CheckPass")
    @Unobstructed
    public JsonResult updateCheckPass(@RequestBody UpdateCheckPassDto updateCheckPassDto){
        Business business = new Business();
        business.setId(updateCheckPassDto.getShopId());
        int check = updateCheckPassDto.getPass()?ShopCheckNum.CHECK_PASS.getCode():ShopCheckNum.CHECK_NOT_PASS.getCode();
        business.setCheckPass(check);

        //删除redis缓存
        businessRedisService.deleteShopById(updateCheckPassDto.getShopId());

        //删除商家登录的凭证
        loginCache.deleteBusiness(updateCheckPassDto.getShopId());

        businessService.updateById(business);
        return ResultTool.success();
    }

    /**
     * 注册商家 addShopDto
     * @param
     * @return
     */
    @PostMapping("/register")
    @Unobstructed
    public JsonResult register(@RequestBody Business business){
        String num = StringUtil.getNum(6);
        business.setAccount(num);
        businessService.save(business);
        return ResultTool.success(num);
    }


    /**
     * 获取未审核的商家列表[分页处理]
     * @param pagenum
     * @param size
     * @return
     */
    @GetMapping("/list/NotCheck/")
    @Unobstructed
    public JsonResult getNoCheck(@PathParam("pagenum") int pagenum,@PathParam("size") int size){
        return ResultTool.success(businessService.getCheckStateList(pagenum,size,ShopCheckNum.NO_CHECK));
    }


    /**
     * 获取审核未通过的商家列表[分页处理]
     * @param pagenum
     * @param size
     * @return
     */
    @GetMapping("/list/CheckNotPass/")
    @Unobstructed
    public JsonResult CheckNotPass(@PathParam("pagenum") int pagenum,@PathParam("size") int size){
        return ResultTool.success(businessService.getCheckStateList(pagenum,size,ShopCheckNum.CHECK_NOT_PASS));

    }

    /**
     * 获取所有的商家列表 [分页]
     */
    @GetMapping("/list/")
    @Unobstructed
    public JsonResult getAll(@PathParam("pagenum") int pagenum,@PathParam("size") int size){
        return ResultTool.success(businessService.getAll(pagenum,size));
    }


    @PutMapping("/")
    @Unobstructed
    public JsonResult update(@RequestBody UpdateShopDto updateShopDto){
        log.info("updateShopDto:{}",updateShopDto);
        Business business = new Business();
        CommonBeanUtils.copyProperties(updateShopDto, business);
        businessService.updateById(business);

        //从数据库中获取
        Business shop = businessService.getById(updateShopDto.getId());
        //先添加
        ElasticSearchShopVo elasticSearchShopVo = new ElasticSearchShopVo();
        elasticSearchShopVo.setId(shop.getId());
        elasticSearchShopVo.setLongitude(shop.getLongitude());
        elasticSearchShopVo.setLatitude(shop.getLatitude());
        elasticSearchShopVo.setName(shop.getName());
        shopListSearchDao.addShop(elasticSearchShopVo);
        //写入到es当中 再更新
        shopListSearchDao.updateShopData(shop.getId(),shop.getName(),shop.getLongitude(), shop.getLatitude());


        //删除redis缓存
        businessRedisService.deleteShopById(updateShopDto.getId());


        return ResultTool.success(null);
    }


}

