package com.cx.springboot02.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.springboot02.dto.AddressVo;
import com.cx.springboot02.mapper.AddressMapper;
import com.cx.springboot02.pojo.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> {

    @Autowired
    AddressMapper addressMapper;


    public int saveAddress(Address address) {
    return addressMapper.insert(address);
    }

    public List<AddressVo> getAddressByCid(Long cid){
        List<AddressVo> addressVos = new ArrayList<>();
        List<Address> list = addressMapper.selectAddressByCid(cid);
        for (Address address : list) {
            AddressVo addressVo = new AddressVo();
            BeanUtils.copyProperties(address, addressVo);
            //判断是否为有效地址
            addressVo.setIsDeliverable(true);
            addressVos.add(addressVo);
        }
        return addressVos;
    }

    public boolean deleteByIdAndCid(Long id,Long cid){
        addressMapper.deleteByIdAndCid(id,cid);
        return true;
    }




}
