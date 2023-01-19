package com.cx.springboot02.controller;


import com.cx.springboot02.common.E.AuthorizeType;
import com.cx.springboot02.common.E.ResultCode;
import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.redis.RedisOperator;
import com.cx.springboot02.common.redis.loginCache.LoginCache;
import com.cx.springboot02.common.utils.Final;
import com.cx.springboot02.common.utils.JwtUtils;
import com.cx.springboot02.common.utils.StringUtil;
import com.cx.springboot02.common.utils.Unobstructed;
import com.cx.springboot02.data.vo.IdentityCheckVo;
import com.cx.springboot02.dto.ManageAndBusinessDto;
import com.cx.springboot02.mapper.BusinessMapper;
import com.cx.springboot02.mapper.ManagerMapper;
import com.cx.springboot02.pojo.Business;
import com.cx.springboot02.pojo.Manager;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("/manager")
@Slf4j
public class ManagerController {
    @Autowired
    ManagerMapper managerMapper;
    @Autowired
    BusinessMapper businessMapper;
    @Autowired
    RedisOperator redisOperator;


    @Autowired
    LoginCache loginCache;

    @PostMapping("/login")
    @Unobstructed
    public JsonResult<Object> login(@RequestBody Map<String,Object> mp, HttpServletRequest request, HttpServletResponse response){
        String username = (String) mp.get("username");
        String password = (String) mp.get("password");
        String userType = (String) mp.get("userType");
        log.info("参数信息:{} {} {}",username,password,userType);
        //验证参数是否为空
        if(StringUtil.isBlank(username) || StringUtil.isBlank(password) || StringUtil.isBlank(userType)) {
//            return new JsonResult<>(false, ResultCode.PARAM_IS_BLANK);
            return ResultTool.fail(ResultCode.PARAM_IS_BLANK);
        }
        //判断身份类型
        if(userType.equals("1")){
            //去admin去查找这个管理员
            Manager manager = managerMapper.checkManage(username, password);
            if(manager!=null){
                //删除之前的token
                loginCache.deleteManage(manager.getId());
                //生成manager的token
                String token = JwtUtils.createToken(username, password, AuthorizeType.MANAGE.identity());
                //存入redis
                loginCache.saveManageToken(token, manager);
                //放入请求头
                response.addHeader("Access-Control-Expose-Headers", "token");
                response.addHeader("token", token);
                manager.setPassword(null);
                ManageAndBusinessDto<Manager> manageAndBusinessDto = new ManageAndBusinessDto<>();
                manageAndBusinessDto.setData(manager);
                manageAndBusinessDto.setType(AuthorizeType.MANAGE.identity());
                manageAndBusinessDto.setToken(token);
                log.info("管理员{}"+"已经登录",manager);
                return ResultTool.success(manageAndBusinessDto);
            }else{
                return ResultTool.fail(ResultCode.USER_CREDENTIALS_ERROR);
            }
        }else if(userType.equals("2")){
            //去business查找
            //去business去查找这个管理员
            Business business = businessMapper.checkBusiness(username, password);
            if(business!=null){
                log.info("商家账号{}是否被禁用:{}",business.getAccount(),business.getDeactivate());
                if(business.getDeactivate()){
                    return ResultTool.fail(ResultCode.ACCOUNT_HAVE_BE_BAN);
                }
                if(business.getCheckPass()!=1){
                    return ResultTool.fail(ResultCode.CHECK_NOT_PASS);
                }
                //删除之前的token
                loginCache.deleteManage(business.getId());
                //生成token
                String token = JwtUtils.createToken(username, password, AuthorizeType.BUSINESS.identity());
                //存入redis
                String redisKey = Un(REDIS_ELEME,REDIS_SAVE_LOGIN_INFO,BUSINESS_TOKEN,token);
                redisOperator.setObject(redisKey, business,USERINFO_LIVE_TIME);
                //放入请求头
                response.addHeader("Access-Control-Expose-Headers", "token");
                response.addHeader("token", token);
                business.setPassword(null);
                ManageAndBusinessDto<Business> manageAndBusinessDto = new ManageAndBusinessDto<>();
                manageAndBusinessDto.setData(business);
                manageAndBusinessDto.setToken(token);
                manageAndBusinessDto.setType(AuthorizeType.BUSINESS.identity());
                log.info("商家{}"+"已经登录",business);
                return ResultTool.success(business);
            }else{
                return ResultTool.fail(ResultCode.USER_CREDENTIALS_ERROR);
            }
        }else{
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }
    }


    /**
     * 根据token查验身份
     * @param mp
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/check")
    @Unobstructed
    public JsonResult<IdentityCheckVo> check(@RequestBody Map<String,Object> mp, HttpServletRequest request, HttpServletResponse response){
        String token = (String) mp.get("token");
        AuthorizeType identity = AuthorizeType.StringToAuthorizeType(JwtUtils.getValue(token, "identity"));
        if(identity!=null) {
            String name = JwtUtils.getValue(token, "name");
            String password = JwtUtils.getValue(token, "password");
            if(identity.equals(AuthorizeType.BUSINESS)){
                IdentityCheckVo<Business> identityCheckVo = new IdentityCheckVo<>();
                Business business = businessMapper.checkBusiness(name,password);
                business.setPassword(null);
                identityCheckVo.setDetail(business);
                identityCheckVo.setIdentity(AuthorizeType.BUSINESS.identity());
                return ResultTool.success(identityCheckVo);
            }else if(identity.equals(AuthorizeType.MANAGE)){
                IdentityCheckVo<Manager> identityCheckVo = new IdentityCheckVo<>();
                Manager manager = managerMapper.checkManage(name,password);
                manager.setPassword(null);
                identityCheckVo.setDetail(manager);
                identityCheckVo.setIdentity(AuthorizeType.MANAGE.identity());
                return ResultTool.success(identityCheckVo);
            }else{
                return ResultTool.fail(ResultCode.COMMON_FAIL);
            }
        } else{
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }
    }


    /**
     * 商家 管理登出
     * @return
     */
    @PostMapping("/loginOut")
    public JsonResult<Object> loginOut(Map<String,Object> mp){
        String token = (String) mp.get("token");
        log.info("拥有token为{}的用户已经成功退出登录",token);
        //删除redis当中的token
        //...
        return ResultTool.success(null);
    }

}

