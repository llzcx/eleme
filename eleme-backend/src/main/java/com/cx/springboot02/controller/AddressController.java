package com.cx.springboot02.controller;


import com.cx.springboot02.common.E.ResultCode;
import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.utils.DateUtils;
import com.cx.springboot02.common.utils.StringUtil;
import com.cx.springboot02.common.utils.Unobstructed;
import com.cx.springboot02.dto.AddAddressDto;
import com.cx.springboot02.dto.AddressVo;
import com.cx.springboot02.dto.UpdateAddressDto;
import com.cx.springboot02.mapper.AddressMapper;
import com.cx.springboot02.pojo.Address;
import com.cx.springboot02.service.impl.AddressServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
@Slf4j
public class AddressController {

    @Autowired
    AddressServiceImpl addressService;

    @Autowired
    AddressMapper addressMapper;

    @Unobstructed
    @PostMapping("/save")
    public JsonResult<Object> save(@RequestBody AddAddressDto addAddressDto) {
        try {
            Address address = new Address();
            BeanUtils.copyProperties(addAddressDto,address);
            address.setCreateTime(DateUtils.getCurrentTime());
            Map<String,Object> mp = new HashMap<>();
            mp.put("cid", addAddressDto.getCid());
            List<Address> list = addressMapper.selectByMap(mp);
            //第一次插入地址 不管是否设置默认 都直接变成默认地址
            if(list.size()==0){
                address.setIsDefault(true);
            }else if(list.size()!=0 && addAddressDto.getIsDefault()){
                //非第一次,但是用户需要设置为默认地址
                //检查这个用户是否已经存在默认地址
                for (Address address1 : list) {
                    if(address1.getIsDefault()){
                        //已经存在 将原来的设置为非默认
                        address1.setIsDefault(false);
                        addressMapper.updateById(address1);
                        break;
                    }
                }
            }else{
                address.setIsDelete(false);
            }
            addressMapper.insert(address);

            return ResultTool.success();
        } catch (DataIntegrityViolationException e){
            return ResultTool.fail(ResultCode.PARAM_NOT_COMPLETE);
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResultTool.fail(ResultCode.PARAM_NOT_VALID);
        }
    }


    @DeleteMapping("/{aid}")
    @Unobstructed
    public JsonResult<String> delete(@PathVariable String aid) {
        try {
            addressService.removeById(aid);
            return ResultTool.success();
        } catch (Exception exception) {
            return ResultTool.fail(ResultCode.PARAM_NOT_VALID);
        }
    }





    @PutMapping("/update")
    @Unobstructed
    public JsonResult<String> delete(@RequestBody UpdateAddressDto updateAddressDto) {
        try {
            Address address = new Address();
            BeanUtils.copyProperties(updateAddressDto, address);
            addressService.updateById(address);
            return ResultTool.success();
        } catch (Exception exception) {
            return ResultTool.fail(ResultCode.PARAM_NOT_VALID);
        }
    }



    @GetMapping("/{cid}")
    @Unobstructed
    public JsonResult<List<AddressVo> > select(@PathVariable Long cid) {
        try {
            List<AddressVo> list = addressService.getAddressByCid(cid);
            return ResultTool.success(list);
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }
    }


    @PutMapping("/setDefault/{aid}")
    @Unobstructed
    public JsonResult<Object > setDefault(@PathVariable String aid) {
        try {

            Address address = addressMapper.selectById(Long.valueOf(aid));
            addressMapper.updateDefaultByCId(address.getCid());
            address.setIsDefault(true);
            addressMapper.updateById(address);
            return ResultTool.success();
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }
    }








}
