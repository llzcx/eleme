package com.cx.springboot02.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.common.redis.RedisOperator;
import com.cx.springboot02.mapper.ManagerMapper;
import com.cx.springboot02.pojo.Business;
import com.cx.springboot02.pojo.Manager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.cx.springboot02.common.utils.Final.*;
import static com.cx.springboot02.common.utils.Final.USERINFO_LIVE_TIME;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 陈翔
 * @since 2022-10-05
 */
@Service
@Slf4j
public class ManagerServiceImpl extends ServiceImpl<ManagerMapper, Manager> {

    @Autowired
    ManagerMapper managerMappera;

    @Autowired
    RedisOperator redisOperator;


    /**
     * 拦截器认证
     * @return
     */
    public Boolean login(String token,String name,String password){
        String redisKey = Un(REDIS_ELEME,REDIS_SAVE_LOGIN_INFO,MANAGE_TOKEN,token);
        try {
            Manager manager1 = (Manager) redisOperator.getObject(redisKey,Manager.class);
            if(manager1 != null) {
                if(name.equals(manager1.getName()) && password.equals(manager1.getPassword())){
                    //刷新redis存在时间
                    redisOperator.expire(redisKey, USERINFO_LIVE_TIME);
                }else{
                    return false;
                }
                //在redis当中有存储
                return true;
            } else{
                return false;
            }
        } catch (ClassCastException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public Manager getBusinessByAAndP(String name, String password){
        return managerMappera.checkManage(name,password);
    }
}
