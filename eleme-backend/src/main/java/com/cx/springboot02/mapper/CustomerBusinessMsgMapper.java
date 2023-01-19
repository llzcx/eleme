package com.cx.springboot02.mapper;

import com.cx.springboot02.data.vo.ChatCustomerListVo;
import com.cx.springboot02.data.vo.CustomerBusinessMsgVo;
import com.cx.springboot02.pojo.CustomerBusinessMsg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import javax.websocket.server.PathParam;
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
public interface CustomerBusinessMsgMapper extends BaseMapper<CustomerBusinessMsg> {
    List<CustomerBusinessMsgVo> selectMsgList(@PathParam("offset") Integer offset, @PathParam("pagesize")Integer pagesize, @PathParam("customerId")Long customerId, @PathParam("businessId")Long businessId);
    Integer selectListNum(@PathParam("customerId")Long customerId, @PathParam("businessId")Long businessId);

    List<ChatCustomerListVo> getCustomerList(@PathParam("bid")Long bid);
}
