package com.cx.springboot02.common.redis;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cx.springboot02.common.utils.Final;
import com.cx.springboot02.common.utils.JedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

import static com.cx.springboot02.common.utils.Final.RELEASE_SUCCESS;


/**
 * 
 * @Title: RedisOperator.java
 * @Package com.weiz.util
 * @Description: 使用redisTemplate的操作实现类 Copyright: Copyright (c) 2016
 * 
 * @author weiz
 * @date 2017年9月29日 下午2:25:03
 * @version V1.0
 */
@Component
@Slf4j
public class RedisOperator {
   
// @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
   @Resource
   private StringRedisTemplate stringRedisTemplate;

   @Resource
   private RedisTemplate redisTemplate;

   private static  final ObjectMapper mapper = new ObjectMapper();

   // Key（键），简单的key-value操作

   /**
    * 实现命令：TTL key，以秒为单位，返回给定 key的剩余生存时间(TTL, time to live)。
    * 
    * @param key
    * @return
    */
   public long ttl(String key) {
      return stringRedisTemplate.getExpire(key);
   }
   
   /**
    * 实现命令：expire 设置过期时间，单位秒
    * 
    * @param key
    * @return
    */
   public void expire(String key, long timeout) {
      stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
   }
   
   /**
    * 实现命令：INCR key，增加key一次
    * 
    * @param key
    * @return
    */
   public long incr(String key, long delta) {
      return stringRedisTemplate.opsForValue().increment(key, delta);
   }

   /**
    * 实现命令：KEYS pattern，查找所有符合给定模式 pattern的 key
    */
   public Set<String> keys(String pattern) {
      return stringRedisTemplate.keys(pattern);
   }

   /**
    * 实现命令：DEL key，删除一个key
    *
    * @param key
    * @return
    */
   public Boolean del(String key) {

      return stringRedisTemplate.delete(key);
   }

   // String（字符串）

   /**
    * 实现命令：SET key value，设置一个key-value（将字符串值 value关联到 key）
    * 
    * @param key
    * @param value
    */
   public void set(String key, String value) {
      stringRedisTemplate.opsForValue().set(key, value);
   }

   /**
    * 实现命令：SET key value EX seconds，设置key-value和超时时间（秒）
    * 
    * @param key
    * @param value
    * @param timeout
    *            （以秒为单位）
    */
   public void set(String key, String value, long timeout) {
      stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
   }

   /**
    * 实现命令：GET key，返回 key所关联的字符串值。
    * 
    * @param key
    * @return value
    */
   public String get(String key) {
      return (String) stringRedisTemplate.opsForValue().get(key);
   }

   // Hash（哈希表）

   /**
    * 实现命令：HSET key field value，将哈希表 key中的域 field的值设为 value
    * 
    * @param key
    * @param field
    * @param value
    */
   public void hset(String key, String field, Object value) {
      stringRedisTemplate.opsForHash().put(key, field, value);
   }

   /**
    * 实现命令：HGET key field，返回哈希表 key中给定域 field的值
    * 
    * @param key
    * @param field
    * @return
    */
   public String hget(String key, String field) {
      return (String) stringRedisTemplate.opsForHash().get(key, field);
   }

   /**
    * 实现命令：HDEL key field [field ...]，删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
    * 
    * @param key
    * @param fields
    */
   public void hdel(String key, Object... fields) {
      stringRedisTemplate.opsForHash().delete(key, fields);
   }

   /**
    * 实现命令：HGETALL key，返回哈希表 key中，所有的域和值。
    * 
    * @param key
    * @return
    */
   public Map<Object, Object> hgetall(String key) {
      return stringRedisTemplate.opsForHash().entries(key);
   }

   // List（列表）

   /**
    * 实现命令：LPUSH key value，将一个值 value插入到列表 key的表头
    * 
    * @param key
    * @param value
    * @return 执行 LPUSH命令后，列表的长度。
    */
   public long lpush(String key, String value) {
      return stringRedisTemplate.opsForList().leftPush(key, value);
   }

   /**
    * 实现命令：LPOP key，移除并返回列表 key的头元素。
    * 
    * @param key
    * @return 列表key的头元素。
    */
   public String lpop(String key) {
      return (String) stringRedisTemplate.opsForList().leftPop(key);
   }

   /**
    * 实现命令：RPUSH key value，将一个值 value插入到列表 key的表尾(最右边)。
    * 
    * @param key
    * @param value
    * @return 执行 LPUSH命令后，列表的长度。
    */
   public long rpush(String key, String value) {
      return stringRedisTemplate.opsForList().rightPush(key, value);
   }

   public Boolean setObject(String key,Object object,Long timeout){
      stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(object),timeout, TimeUnit.SECONDS);
      return true;
   }

   public Boolean setObject(String key,Object object){
      stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(object));
      return true;
   }

   public <T> T getObject(String key,Class<T> cls){
      return JSONObject.parseObject(stringRedisTemplate.opsForValue().get(key),cls);
   }


   /**
    * 获取指定排名的value
    * @param key
    * @param start
    * @param end
    * @return
    */
   public Set<Object> reverseRange(String key, Integer start, Integer end) {
      try {
         return redisTemplate.opsForZSet().reverseRange(key, start, end);
      } catch (Exception e) {
         log.error("[RedisUtils.rangeZset] [error] [key is {},start is {},end is {}]", key, start, end, e);
         return null;
      }
   }

   /**
    * 增加有序集合
    *
    * @param key
    * @param value
    * @param seqNo
    * @return
    */
   public Boolean addZset(String key, Object value, double seqNo) {
      try {
         return redisTemplate.opsForZSet().add(key, value, seqNo);
      } catch (Exception e) {
         log.error("[RedisUtils.addZset] [error]", e);
         return false;
      }
   }
   /**
    * 查询集合中指定顺序的值， 0 -1 表示获取全部的集合内容  zrange
    *
    * 返回有序的集合，score小的在前面
    *
    * @param key
    * @param start
    * @param end
    * @return
    */
   public Set<String> range(String key, int start, int end) {
      return redisTemplate.opsForZSet().range(key, start, end);
   }
   /**
    * 删除元素 zrem
    *
    * @param key
    * @param value
    */
   public void remove(String key, String value) {
      redisTemplate.opsForZSet().remove(key, value);
   }


   public Boolean incrementScore(String key, Object value, double seqNo) {
      try {
         redisTemplate.opsForZSet().incrementScore(key, value, seqNo);
         return true;
      } catch (Exception e) {
         log.error("[RedisUtils.addZset] [error]", e);
         return false;
      }
   }

   public double getScore(String key, Object value) {
      try {
         return redisTemplate.opsForZSet().score(key, value);
      } catch (Exception e) {
         log.error("[RedisUtils.addZset] [error]", e);
         return 0;
      }
   }

   /**
    * 检查是否存在
    * @param key
    * @param value
    * @return
    */
   public Boolean checkZsetValueIsExist(String key,String value){
      return  redisTemplate.opsForZSet().score(key, value)!=null;

   }

   public boolean exists(String key) {
      return redisTemplate.hasKey(key);
   }

   /**
    * 判断key是否过期
    * @param key
    * @return
    */
   public boolean isExpire(String key) {
      return ttl(key) > 0?false:true;
   }

   public void addList(String key,List<String> list){
      redisTemplate.opsForList().rightPush(key,list);
   }

   public List<String> getList(String key){
      return redisTemplate.opsForList().range(key, 0, -1); // 获取所有的
   }

   /**
    * 加锁，无阻塞
    * @param key 锁
    * @param requestId 请求标识
    * @param expireTime 超期时间
    * @return 是否获取成功
    */
   public Boolean lock(String key, long expireTime,String requestId) {
      Long start = System.currentTimeMillis();
      //在一定时间内获取锁，超时则返回错误
      for (;;){
         //Set命令返回OK，则证明获取锁成功
         Boolean ret = redisTemplate.opsForValue().setIfAbsent( key, requestId, expireTime, TimeUnit.SECONDS);
         if (ret) {
            return true;
         }
         //否则循环等待，在timeout时间内仍未获取到锁，则获取失败
         long end = System.currentTimeMillis() - start;
         if (end >= Final.LOCK_TIME){
            return false;
         }
      }
   }
   /**
    * 释放分布式锁
    * @param lockKey 锁
    * @param requestId 请求标识
    * @return 是否释放成功
    */
   public static boolean releaseDistributedLock(String lockKey, String requestId) {
      Jedis jedis = JedisUtil.redisPoolFactory().getResource();
      String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
      Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
      if (RELEASE_SUCCESS.equals(result)) {
         return true;
      }
      return false;
   }


}