package com.cx.springboot02;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cx.springboot02.common.RPage;
import com.cx.springboot02.common.elasticsearch.ElasticSearchIKWord;
import com.cx.springboot02.dto.AddSpuDto;
import com.cx.springboot02.mapper.*;
import com.cx.springboot02.pojo.*;
import com.cx.springboot02.service.impl.BusinessServiceImpl;
import com.cx.springboot02.service.impl.CustomerServiceImpl;
import com.cx.springboot02.service.impl.GaoDeMapServiceImpl;
import com.cx.springboot02.service.impl.GoodsServiceImpl;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.Index;
import org.apache.http.util.EntityUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.rocketmq.common.message.Message;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.XMLFormatter;

//解决websocket安装以后redis测试报错javax.websocket.server.ServerContainer not available
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class SSMPApplicationTests {

    @Autowired
    DataSource dataSource;

    @Test
    void contextLoads() throws Exception{
        System.out.println(dataSource.getClass());
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
        System.out.println(dataSource.toString());
        //template模板，拿来即用
        connection.close();
    }

    @Autowired
    CustomerServiceImpl customerService;

    @Autowired
    CustomerMapper customerMapper;

    @Test
    public void selectCustomer(){
        String email = "123@qq.com";
        String password = "1234";
        LambdaUpdateWrapper<Customer> userQueryWrapper = Wrappers.<Customer>lambdaUpdate()
                .eq(Customer::getEmail,email);
        Customer customer = new Customer();
        customer.setPassword(password);
        customerMapper.update(customer,userQueryWrapper);

    }


    @Autowired
    BusinessServiceImpl businessService;


    @Autowired
    BusinessMapper businessMapper;

    @Test
    public void fenye1(){
        LambdaQueryWrapper<Business> userLambdaQueryWrapper = Wrappers.lambdaQuery();
//        userLambdaQueryWrapper.like(Business::getShopName , "木桶");

        Page<Business> userPage = new Page<>(1 , 10);
        IPage<Business> userIPage = businessMapper.selectPage(userPage , userLambdaQueryWrapper);
        System.out.println("总页数： "+userIPage.getPages());
        System.out.println("总记录数： "+userIPage.getTotal());
        userIPage.getRecords().forEach(System.out::println);
    }


    @Autowired
    AddressMapper addressMapper;


    @Test
    public void  save() {
        Address address = new Address();
        address.setCid(123L);
        address.setSpecificAddress("123挖省掉呢");
        address.setPhone("12313122222");
        address.setName("name");
        addressMapper.insert(address);
    }

    public void  bis() {
        RPage<Business> businessListPage = businessService.getBusinessListPage(0, 1);

        System.out.println(businessListPage.toString());
    }




    @Autowired
    OrderMapper orderMapper;

    @Test
    public void TR() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("myproducer_group_topic_name_delay_01");
        //设置实例名称，一个jvm中有多个生产者可以根据实例名区分
        //默认default
        producer.setInstanceName("topic_delay");
        // 指定nameserver的地址
        producer.setNamesrvAddr("localhost:9876");
        //设置同步重试次数
        producer.setRetryTimesWhenSendFailed(2);
        //设置异步发送次数
        //producer.setRetryTimesWhenSendAsyncFailed(2);
        // 初始化生产者
        producer.start();
        for (int i = 0; i <20 ; i++) {
            Message message = new Message("topic_name_delay", ("key=" + i).getBytes("utf-8"));
            //设置延迟消费时间 设置延迟时间级别0,18,0表示不延迟，18表示延迟2h，大于18的都是2h
            message.setDelayTimeLevel(i);
            // 1 同步发送  如果发送失败会根据重试次数重试
            SendResult send = producer.send(message);
            SendStatus sendStatus = send.getSendStatus();
            System.out.println(sendStatus.toString());

        }
    }


//    @Resource
//    private RocketMQTemplate rocketMQTemplate;
//
//    @Test
//    public void testRocketMq1() {
//        Order order = new Order();
//        order.setId(1L);
//        rocketMQTemplate.asyncSend("WALLET_ORDER_TOPIC", MessageBuilder.withPayload(order).build(), new SendCallback() {
//            @Override
//            public void onSuccess(SendResult var1) {
//                System.out.println("async onSucess SendResult :{}"+var1);
//            }
//
//            @Override
//            public void onException(Throwable var1) {
//                System.out.println("async onException Throwable :{}"+var1);
//            }
//
//        }, 300000, 2);
//        }
    @Autowired
    GoodsServiceImpl goodsService;
    @Test
    public void ad(){
        goodsService.addSpu(new AddSpuDto());
    }



    @Resource
    RedisTemplate<String,Object> redisTemplate;



    @Test
    public void RedisTest(){
        Customer customer = new Customer();
        customer.setName("小米");
        customer.setAvatar("1231");
        redisTemplate.opsForValue().set("test:data:5", customer);

        Object o = redisTemplate.opsForValue().get("test:data:5");
        Customer customer1 = (Customer) o;
        System.out.println("customer1 = " + customer1);
    }


    @Autowired
    RestHighLevelClient client;

    @Test
    public void addIndex() throws Exception{
        IndicesClient indicesClient = client.indices();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("itheima");
//        CreateIndexResponse createIndexResponse = indicesClient.create(createIndexRequest, RequestOptions.DEFAULT);
//        System.out.println("createIndexResponse = " + createIndexResponse.isAcknowledged());
        String mapping = "{\n" +
                "      \"properties\" : {\n" +
                "        \"address\" : {\n" +
                "          \"type\" : \"text\",\n" +
                "          \"analyzer\" : \"ik_max_word\"\n" +
                "        },\n" +
                "        \"age\" : {\n" +
                "          \"type\" : \"long\"\n" +
                "        },\n" +
                "        \"name\" : {\n" +
                "          \"type\" : \"keyword\"\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        createIndexRequest.mapping(mapping, XContentType.JSON);
        CreateIndexResponse createIndexResponse1 = indicesClient.create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse1.isAcknowledged());
    }



    @Test
    public void queryIndex() throws Exception{
        IndicesClient indicesClient = client.indices();

        GetIndexRequest getIndexRequest = new GetIndexRequest("itheima");
        GetIndexResponse getIndexResponse = indicesClient.get(getIndexRequest, RequestOptions.DEFAULT);

        //获取结果
        Map<String, MappingMetadata> mappings = getIndexResponse.getMappings();
        for (String s : mappings.keySet()) {
            System.out.println(s+":"+mappings.get(s).getSourceAsMap());
        }
    }

    @Test
    public void ik() throws Exception{
        List<String> list = new ArrayList<String>();
        Request request = new Request("GET", "_analyze");
        JSONObject entity = new JSONObject();
        entity.put("analyzer", "ik_max_word");
        //entity.put("analyzer", "ik_smart");
        entity.put("text", "乔碧萝吃西瓜炒饭");
        request.setJsonEntity(entity.toJSONString());
        Response response = this.client.getLowLevelClient().performRequest(request);
        JSONObject tokens = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        JSONArray arrays = tokens.getJSONArray("tokens");
        for (int i = 0; i < arrays.size(); i++)
        {
            JSONObject obj = JSON.parseObject(arrays.getString(i));
            String token = obj.getString("token");
            System.out.println("token = " + token);
            list.add(token);
        }

    }


    @Value("${ik.path}")
    private String POST;


    @Autowired
    ElasticSearchIKWord elasticSearchIKWord;

    @Test
    public void text09(){
        elasticSearchIKWord.GetRemoteThesaurus();
    }

    @Autowired
    GaoDeMapServiceImpl gaoDeMapService;


    @Test
    public void test10(){
        String city = null;
        try {
            city = gaoDeMapService.city("111.580676,26.440583");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        System.out.println("city = " + city);

    }

    @Test
    public void getScore(){
        System.out.println("redisTemplate.opsForZSet().score(\"ELEME:HOT_KEY:永州市\", \"烧烤\") = " + redisTemplate.opsForZSet().score("ELEME:HOT_KEY:永州市", "大鱼"));
    }


    @Test
    public void getCityList(){
        List<String> cityList = gaoDeMapService.getCityList();
        System.out.println("cityList = " + cityList);

    }
    @Resource
    private RedissonClient redissonClient;


    @Test
    public void lock() throws Exception{
        //创建
        RLock helloLock = redissonClient.getLock("hello");

        //加锁
        helloLock.lock();
        try {
            log.info("locked");
            Thread.sleep(1000 * 200);
        } finally {
            //释放锁
            helloLock.unlock();
        }
        log.info("finished");

    }


}
