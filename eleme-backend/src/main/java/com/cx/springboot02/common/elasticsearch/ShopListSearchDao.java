package com.cx.springboot02.common.elasticsearch;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cx.springboot02.data.vo.ElasticSearchShopVo;
import com.cx.springboot02.data.vo.ElasticSearchSpuVo;
import com.cx.springboot02.mapper.BusinessMapper;
import com.cx.springboot02.mapper.GoodsMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cx.springboot02.common.utils.Final.PREFIX_SHOP_ID;
import static com.cx.springboot02.common.utils.Final.PREFIX_SPU_ID;
import static org.elasticsearch.xcontent.XContentFactory.jsonBuilder;

@Component
@Slf4j
public class ShopListSearchDao {

    @Autowired
    GoodsMapper goodsMapper;


    @Autowired
    BusinessMapper businessMapper;


    @Autowired
    RestHighLevelClient client;
    @Data
    class ParentParams{
        private String name;
        private String parent;
        public ParentParams(){

        }
        public ParentParams(String name) {
            this.name = name;
        }

        public ParentParams(String name, String parent) {
            this.name = name;
            this.parent = parent;
        }
    }
    @Data
    static
    class AddShop {
        private String shop_id;
        private String shop_name;
        private String shop_spu;
        private String geohash;
        public AddShop(){

        }
        public AddShop(String shop_id, String shop_name, String shop_spu,String longitude,String latitude) {
            this.shop_id = shop_id;
            this.shop_name = shop_name;
            this.shop_spu = shop_spu;
            this.geohash = latitude +"," + longitude;
        }
    }
    @Data
    static
    class AddSpu {
        private String spu_id;
        private String spu_name;
        private ParentParams shop_spu;
        public AddSpu(){

        }
        public AddSpu(String spu_id, String spu_name, ParentParams shop_spu) {
            this.spu_id = spu_id;
            this.spu_name = spu_name;
            this.shop_spu = shop_spu;
        }
    }
    //初始化es的数据
    public void initESData() throws Exception{
        List<ElasticSearchShopVo> shops  = businessMapper.selectAllIdAndName();
        for (ElasticSearchShopVo shop : shops) {
            addShop(shop);
        }
        List<ElasticSearchSpuVo> goods = goodsMapper.selectAllIdAndName();
        for (ElasticSearchSpuVo spu : goods) {
            addSpu(spu);
        }
    }

    /**
     *更新商铺的数据
     */
    public void updateShopData(Long shopId,String name,String lon,String lat){
        log.info("本次要更新的各个数据:shopId:{},name:{},lon:{},lat:{}",shopId,name,lon,lat);
        try {
            UpdateRequest updateRequest = new UpdateRequest();
            AddShop addShop = new AddShop();
            addShop.setShop_name(name);
            if(lat != null && lon!=null){
                addShop.setGeohash(lat+","+lon);
            }
            updateRequest.index("wm_shop").id(PREFIX_SHOP_ID +shopId).doc(XContentType.JSON,JSON.toJSONString(addShop));
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *更新商品的数据
     */
    public void updateSpuData(Long spuId,String name){
        try {
            UpdateRequest updateRequest = new UpdateRequest();
            AddSpu addShop = new AddSpu();
            addShop.setSpu_name(name);
            updateRequest.index("wm_shop").id(PREFIX_SPU_ID +spuId).doc(JSON.toJSONString(addShop),XContentType.JSON);
            log.info(PREFIX_SPU_ID +spuId+"更新成功!");
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加商铺
     */
    public void addShop(ElasticSearchShopVo shop){
        try {
            IndexRequest indexRequest = new IndexRequest("wm_shop");
            String json = JSON.toJSONString(new AddShop(PREFIX_SHOP_ID + shop.getId(), shop.getName(), "shop", shop.getLongitude(), shop.getLatitude()));
            log.info("parent:json = {}",json);
            indexRequest.id(PREFIX_SHOP_ID +shop.getId()).source(json, XContentType.JSON);
            IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);
            log.info("index.status() = {}", index.status());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加商品
     */
    public void addSpu(ElasticSearchSpuVo spu){
        try {
            IndexRequest indexRequest = new IndexRequest("wm_shop");
            String json = JSON.toJSONString(new AddSpu(PREFIX_SPU_ID +spu.getId(),spu.getName(),new ParentParams("spu", PREFIX_SHOP_ID +spu.getBusinessId().toString())));
            log.info("son:json = {}",json);
            indexRequest.id(PREFIX_SPU_ID +spu.getId()).source(json, XContentType.JSON);
            indexRequest.routing(PREFIX_SPU_ID +spu.getId().toString());
            IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);
            log.info("index.status() = {}", index.status());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除商铺
     * @param shopId
     */
    public void deleteShop(Long shopId){
        try {
            DeleteRequest deleteRequest = new DeleteRequest("wm_shop", PREFIX_SHOP_ID +shopId);
            DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除商品
     * @param spuId
     */
    public void deleteSku(Long spuId){
        try {
            DeleteRequest deleteRequest = new DeleteRequest("wm_shop", PREFIX_SPU_ID +spuId);
            DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SearchHit[] selectShopList(String lastId,Integer size,Double longitude, Double latitude,String key) throws Exception{
        //先去es查询满足条件的数据列表
        SearchRequest searchRequest = new SearchRequest("wm_shop");

        //构件查询语句 [距离]
        QueryBuilder distanceQuery = QueryBuilders.geoDistanceQuery("geohash").point(latitude, longitude).distance(5000,
                DistanceUnit.METERS);
        //将查询语句整合
        QueryBuilder queryBuilder;
        if(key!=null && !"".equals(key)){
            // 构建查询语句1 [用于根据子查父]
            QueryBuilder hasChildQueryBuilder = JoinQueryBuilders.hasChildQuery(
                    "spu",                         //要查询的子类型
                    (QueryBuilder) QueryBuilders.termQuery("spu_name", key),
                    ScoreMode.None
            ).innerHit(new InnerHitBuilder());
            //构建查询语句2 [根据商家名字]
            QueryBuilder shopQuery = QueryBuilders.matchQuery("shop_name", key);
            queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.boolQuery().should(hasChildQueryBuilder).should(shopQuery))
                    .filter(distanceQuery);
        }else{
            queryBuilder = QueryBuilders.boolQuery().filter(distanceQuery);
        }


        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//必须加上track_total_hits，不然就只显示10000
        sourceBuilder.query(queryBuilder);
        //编辑脚本信息
        Map<String, Object> params = new HashMap<>();//距离脚本参数
        //参数赋值
        params.put("lat", latitude);
        params.put("lon", longitude);
        Script script = new Script(ScriptType.INLINE, "painless",
                "doc['geohash'].arcDistance(params.lat, params.lon)", params);
        sourceBuilder.scriptField("distance_in_meters", script);
        sourceBuilder.fetchSource("*", null);//设置返回所有字段，不设置的话只会返回fields
        //设置分页的searchAfter
        if(lastId!=null){
            String[] searchAfter = new String[]{lastId};
            sourceBuilder.searchAfter(searchAfter);
        }
        // 使用searchAfter需要设置为-1或者0
        sourceBuilder.from(0);
        // 每页多少条数据
        sourceBuilder.size(size);
        // 设置唯一排序值定位 [分页的必要选项 SearchAfter]
        sourceBuilder.sort(SortBuilders.fieldSort("shop_id").order(SortOrder.DESC));
        //将sourceBuilder对象添加到搜索请求中
        searchRequest.source(sourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("ES的搜索结果:"+ JSONObject.toJSON(search));
        SearchHit[] hits = search.getHits().getHits();
        return hits;
    }
}
