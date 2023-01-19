package com.cx.springboot02.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cx.springboot02.pojo.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper  extends BaseMapper<Address> {


    List<Address> selectAddressByCid(@Param("cid") Long cid);

    void deleteByIdAndCid(@Param("id") Long id,@Param("cid") Long cid);

    void updateDefaultByCId(@Param("cid") Long cid);
}
