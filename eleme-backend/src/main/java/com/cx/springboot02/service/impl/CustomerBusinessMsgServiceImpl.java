package com.cx.springboot02.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.common.RPage;
import com.cx.springboot02.data.vo.CustomerBusinessMsgVo;
import com.cx.springboot02.mapper.BusinessMapper;
import com.cx.springboot02.mapper.CustomerMapper;
import com.cx.springboot02.mapper.CustomerBusinessMsgMapper;
import com.cx.springboot02.pojo.Business;
import com.cx.springboot02.pojo.Customer;
import com.cx.springboot02.pojo.CustomerBusinessMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class CustomerBusinessMsgServiceImpl extends ServiceImpl<CustomerBusinessMsgMapper, CustomerBusinessMsg> {

    @Autowired
    CustomerBusinessMsgMapper customer_businessMsgMapper;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    BusinessMapper businessMapper;


    public RPage<CustomerBusinessMsgVo> getList(Integer pagenum, Integer size, Long customerId, Long businessId){
        if(size > 20) size = 20;//最大只允许查询20条
        RPage<CustomerBusinessMsgVo> rPage = new RPage<>(pagenum, size, customer_businessMsgMapper.selectMsgList(size * (pagenum - 1), size, customerId, businessId));
        for (CustomerBusinessMsgVo row : rPage.getRows()) {
            if(row.getCustomerSender()){
                //客户是发送者
                Customer customer = customerMapper.selectById(customerId);
                row.setImagePath(customer.getAvatar());
            }else{
                //客户不是发送者
                Business business = businessMapper.selectById(businessId);
                row.setImagePath(business.getImagePath());
            }

        }
        rPage.SetTotalCountAndTotalPage(customer_businessMsgMapper.selectListNum(customerId, businessId));
        return  rPage;
    }
}
