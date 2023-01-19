package com.cx.springboot02.common.websocket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cx.springboot02.common.E.AuthorizeType;
import com.cx.springboot02.common.utils.CommonBeanUtils;
import com.cx.springboot02.common.utils.DateUtils;
import com.cx.springboot02.data.vo.CustomerBusinessMsgVo;
import com.cx.springboot02.dto.BCMessageDto;
import com.cx.springboot02.mapper.BusinessMapper;
import com.cx.springboot02.mapper.CustomerMapper;
import com.cx.springboot02.mapper.CustomerBusinessMsgMapper;
import com.cx.springboot02.pojo.Business;
import com.cx.springboot02.pojo.Customer;
import com.cx.springboot02.pojo.CustomerBusinessMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}")  // 接口路径 ws://localhost:8080/webSocket/userId;
public class ShopChatWebSocket {
    
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
        /**
     * 用户ID
     */
    private String userId;

    /**
     * 各个身份的前缀
     */
    private final String businessQZ = AuthorizeType.BUSINESS.identity();
    private final String customerQZ = AuthorizeType.CUSTOMER.identity();

    @Autowired
    public void setCustomerBusinessMsgMapper(CustomerBusinessMsgMapper customerBusinessMsgMapper) { ShopChatWebSocket.customerBusinessMsgMapper = customerBusinessMsgMapper; }
    @Autowired
    public void setCustomerMapper(CustomerMapper customerMapper) { ShopChatWebSocket.customerMapper = customerMapper; }
    @Autowired
    public void setBusinessMapper(BusinessMapper businessMapper) { ShopChatWebSocket.businessMapper = businessMapper; }

    private static CustomerMapper customerMapper;
    private static CustomerBusinessMsgMapper customerBusinessMsgMapper;
    private static BusinessMapper businessMapper;
    
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    //  注：底下WebSocket是当前类名
    private static CopyOnWriteArraySet<ShopChatWebSocket> shopChatWebSockets =new CopyOnWriteArraySet<>();

    // 用来存在线连接用户信息
    private static ConcurrentHashMap<String,Session> sessionPool = new ConcurrentHashMap<String,Session>();

    //获取客户端连接的session 看这个客户是否已经连上
    public Session getCustomerSession(String customerId){
        return sessionPool.get(customerQZ+customerId);
    }
    //获取商家端连接的session 看这个商家是否已经连上
    public Session getBusinessSession(String customerId){
        return sessionPool.get(businessQZ+customerId);
    }
    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value="userId")String userId) {
        try {
			this.session = session;
			this.userId = userId;
			shopChatWebSockets.add(this);
			sessionPool.put(userId, session);
			log.info("【websocket消息】有新的连接:{},{}，总数为:"+ shopChatWebSockets.size(),userId,session.getId());
			//广播
//            String message = "["+userId+"]进入聊天室";
//            sendAllMessage(message);
		} catch (Exception e) {
		}
    }
    
    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
			shopChatWebSockets.remove(this);
			sessionPool.remove(this.userId);
			log.info("【websocket消息】连接断开，总数为:"+ shopChatWebSockets.size());
		} catch (Exception e) {
		}
    }
    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
    	log.info("【websocket消息】收到客户端消息:"+message);
        try {
            //将前端传来的json数据解析为java对象
            JSONObject parse = JSONObject.parseObject(message);
            BCMessageDto bcMessageDto = parse.toJavaObject(BCMessageDto.class);
            //将javabean对象赋值
            CustomerBusinessMsg customerBusinessMsg = new CustomerBusinessMsg();
            customerBusinessMsg.setBusinessId(Long.valueOf(bcMessageDto.getBusinessId()));
            customerBusinessMsg.setCustomerId(Long.valueOf(bcMessageDto.getCustomerId()));
            customerBusinessMsg.setContent(bcMessageDto.getContent());
            customerBusinessMsg.setCreateTime(DateUtils.getCurrentTime());
            customerBusinessMsg.setIsDelete(false);
            String identity = bcMessageDto.getIdentity();
            if(AuthorizeType.BUSINESS.identity().equals(identity)){
                //身份是商家的时候
                customerBusinessMsg.setCustomerSender(false);
                //存储在数据库 mp的insert以后会直接赋值给主键
                customerBusinessMsgMapper.insert(customerBusinessMsg);
                Session oppositeSideSession = getCustomerSession(bcMessageDto.getCustomerId());
                if(oppositeSideSession!=null){
                    //在线,将消息转发
                    //将消息转发给该客户,如果该客户在线上 [这个时候发给前端的消息时一端JSON字符串,需要前端解析]
                    //转换成vo发送给前端
                    CustomerBusinessMsgVo customerBusinessMsgVo = new CustomerBusinessMsgVo();
                    CommonBeanUtils.copyProperties(customerBusinessMsg, customerBusinessMsgVo);
                    Customer customer = customerMapper.selectById(customerBusinessMsg.getCustomerId());
                    customerBusinessMsgVo.setImagePath(customer.getAvatar());
                    sendCustomerMessage(bcMessageDto.getCustomerId(),JSON.toJSONString (customerBusinessMsgVo));
                    log.info("客户端:{},成功接收到消息",bcMessageDto.getCustomerId());
                }else{
                    //不在线
                    log.info("客户端:{}不在线,无法将收到的消息转发",bcMessageDto.getCustomerId());
                }
                //将消息返回给发送者
                CustomerBusinessMsgVo customerBusinessMsgVo = new CustomerBusinessMsgVo();
                CommonBeanUtils.copyProperties(customerBusinessMsg,customerBusinessMsgVo);
                Business business = businessMapper.selectById(customerBusinessMsg.getBusinessId());
                customerBusinessMsgVo.setImagePath(business.getImagePath());
                sendBusinessMessage(bcMessageDto.getBusinessId(),JSON.toJSONString (customerBusinessMsgVo));
            }else if(AuthorizeType.CUSTOMER.identity().equals(identity)){
                //身份是顾客的时候
                customerBusinessMsg.setCustomerSender(true);
                //存储在数据库
                customerBusinessMsgMapper.insert(customerBusinessMsg);
                //将消息转发给该商家[对面的],如果该商家在线上
                Session oppositeSideSession = getBusinessSession(bcMessageDto.getBusinessId());
                if(oppositeSideSession!=null){
                    //在线,将消息转发
                    //将消息转发给该客户,如果该客户在线上 [这个时候发给前端的消息时一端JSON字符串,需要前端解析]
                    CustomerBusinessMsgVo customerBusinessMsgVo = new CustomerBusinessMsgVo();
                    CommonBeanUtils.copyProperties(customerBusinessMsg,customerBusinessMsgVo);
                    Customer customer = customerMapper.selectById(customerBusinessMsg.getCustomerId());
                    customerBusinessMsgVo.setImagePath(customer.getAvatar());
                    sendBusinessMessage(bcMessageDto.getBusinessId(),JSON.toJSONString (customerBusinessMsgVo));
                    log.info("商家端:{},成功接收到消息",bcMessageDto.getCustomerId());
                }else{
                    //不在线
                    log.info("商家端:{}不在线,无法将收到的消息转发",bcMessageDto.getCustomerId());
                }
                //将消息返回给发送者
                CustomerBusinessMsgVo customerBusinessMsgVo = new CustomerBusinessMsgVo();
                CommonBeanUtils.copyProperties(customerBusinessMsg,customerBusinessMsgVo);
                Customer customer = customerMapper.selectById(customerBusinessMsg.getCustomerId());
                customerBusinessMsgVo.setImagePath(customer.getAvatar());
                sendCustomerMessage(bcMessageDto.getCustomerId(),JSON.toJSONString (customerBusinessMsgVo));
            }else{
                log.info("[身份无法识别{}]",identity);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }
    
	  /** 发送错误时的处理
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {

        log.error("用户错误,原因:"+error.getMessage());
        error.printStackTrace();
    }

    
    // 此为广播消息
    public void sendAllMessage(String message) {
    	log.info("【websocket消息】广播消息:"+message);
        for(ShopChatWebSocket shopChatWebSocket : shopChatWebSockets) {
            try {
            	if(shopChatWebSocket.session.isOpen()) {
            		shopChatWebSocket.session.getAsyncRemote().sendText(message);
            	}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // 此为单点消息
    public void sendOneMessage(String userId, String message) {
        Session session = sessionPool.get(userId);
        if (session != null&&session.isOpen()) {
            try {
            	log.info("【websocket消息】 单点消息:"+message);
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 单点 [发送消息给某个客户端]
    public void sendCustomerMessage(String userId, String message) {
        Session session = sessionPool.get(customerQZ+userId);
        if (session != null&&session.isOpen()) {
            try {
                log.info("【websocket消息】 单点消息:"+message);
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            log.info("无法向用户:{}推送消息,原因:不在线",userId);
        }
    }

    // 单点 [发送消息给某个商家daunt]
    public void sendBusinessMessage(String userId, String message) {
        Session session = sessionPool.get(businessQZ+userId);
        if (session != null&&session.isOpen()) {
            try {
                log.info("【websocket消息】 单点消息:"+message);
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            log.info("无法向商家:{}推送消息,原因:不在线",userId);
        }
    }
    
    // 此为单点消息(多人)
    public void sendMoreMessage(String[] userIds, String message) {
    	for(String userId:userIds) {
    		Session session = sessionPool.get(userId);
            if (session != null&&session.isOpen()) {
                try {
                	log.info("【websocket消息】 单点消息:"+message);
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    	}
        
    }

}
