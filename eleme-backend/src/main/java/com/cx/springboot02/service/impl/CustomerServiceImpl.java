package com.cx.springboot02.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.common.redis.RedisOperator;
import com.cx.springboot02.common.utils.Final;
import com.cx.springboot02.mapper.CustomerMapper;
import com.cx.springboot02.pojo.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static com.cx.springboot02.common.utils.Final.*;

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
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer>  {
    @Autowired
    public CustomerMapper customerMapper;

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 拦截器认证
     * @return
     */
    public Boolean login(String token,String name,String password){
        String redisKey = Un(REDIS_ELEME,REDIS_SAVE_LOGIN_INFO,CUSTOM_TOKEN,token);
        try {
            Customer customer1 =  redisOperator.getObject(redisKey,Customer.class);
            if(customer1 != null) {
                if(name.equals(customer1.getName()) && password.equals(customer1.getPassword())){
                    //刷新redis存在时间
                    redisOperator.expire(redisKey, USERINFO_LIVE_TIME);
                }else{
                    return false;
                }
                //在redis当中有存储
                return true;
            } else{
                //redis不存在直接返回false
               return false;
            }
        } catch (ClassCastException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public Customer getCustomerByAAndP(String name, String password){
        return customerMapper.checkCustomer(name, password);
    }


    /**
     * 通过customer的name属性查询一个Customer对象
     * @param name
     * @return
     */
    public Customer selectCustomerByName(String name) {
        return customerMapper.selectCustomerByName(name);
    }


    /**
     * 注册
     */
    public void register(Customer customer) {
        customer.setId(null);
        customerMapper.insert(customer);
    }


    /**
     * 根据邮箱更新password
     * @param email
     * @param password
     */
    public void forget(String email,String password) {
        LambdaUpdateWrapper<Customer> userQueryWrapper = Wrappers.<Customer>lambdaUpdate()
                .eq(Customer::getEmail,email);
        Customer customer = new Customer();
        customer.setPassword(password);
        customerMapper.update(customer,userQueryWrapper);
    }

    public List<Customer> selectCustomerByEmail(String email){
        HashMap<String,Object> map=new HashMap<>();
        //自定义查询
        map.put("email",email);
        return customerMapper.selectByMap(map);
    }

    /**
     * 根据id更新头像
     * @param id
     * @param url
     */
    public void setImage(Long id,String url){
        Customer customer = new Customer();
        customer.setId(id);
        customer.setAvatar(url);
        customerMapper.updateById(customer);
    }
}
