package com.cx.springboot02.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cx.springboot02.common.gaode.District;
import com.cx.springboot02.common.gaode.DistrictResponse;
import com.cx.springboot02.common.redis.RedisOperator;
import com.cx.springboot02.common.utils.Unobstructed;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.cx.springboot02.common.redis.RedisFinal.CITY_LIST;
import static com.cx.springboot02.common.redis.RedisFinal.ELEME;


@Service
@Slf4j
public class GaoDeMapServiceImpl {


    public final String key = "666cf07e826b62760407bf8068855003";
    public final String http = "https://restapi.amap.com";

    @Autowired
    RedisOperator redisOperator;

    @Resource
    private RedissonClient redissonClient;

    public String request(String url) throws Exception {
        URL localURL = new URL(url);
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        //响应失败
        if (httpURLConnection.getResponseCode() >= 300) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }
        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            return resultBuffer.toString();
        }
    }

    private String addURLParam(String name, Object value){
        return "&"+name+"="+value.toString();
    }


    /**
     * 根据key 获取详情
     *
     * @param palceS
     * @return
     * @throws Exception
     */
    public String getTips(String palceS) throws Exception {
        String url = http + "/v3/assistant/inputtips?keywords=" + palceS + "&key=" + key;
        return request(url);
    }



    @Data
    static class RegeoData{
        private Integer status;
        private Regeocode regeocode;
    }
    @Data
    static class Regeocode{
        private List<Road> roads;
        private List<Roadinter> roadinters;
        private String formatted_address;
        private AddressComponent addressComponent;
    }
    @Data
    static class Road{

    }
    @Data
    static class Roadinter{

    }
    @Data
    static class AddressComponent{
        private String city;
        private String province;
        private String adcode;
        private String district;
        private String towncode;
        private String country;
        private String township;
    }
    /**
     * 逆向编码
     *
     * @param geohash
     * @return
     * @throws Exception
     */
    public String regeo(String geohash) throws Exception {
        String sb = http + "/v3/geocode/regeo?" +
                addURLParam("key", key) +
                addURLParam("location", geohash) +
                addURLParam("extensions", "all");
        return request(sb);
    }


    public String city(String geohash)  {
        try {
            String sb = http + "/v3/geocode/regeo?" +
                    addURLParam("key", key) +
                    addURLParam("location", geohash) +
                    addURLParam("extensions", "all");
            String response = request(sb);
            RegeoData regeoData = JSONObject.parseObject(response, RegeoData.class);
            return regeoData.getRegeocode().getAddressComponent().getCity();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }


    /**
     * 从高德地图里面获取所有的城市
     * @return
     */
    public List<String> getCityList(){
        String key = ELEME+CITY_LIST;
        List<String> cityList = new ArrayList<>();
        try {
            if(!redisOperator.exists(key)){
                String sb = http + "/v3/config/district?" +
                        addURLParam("key", key) +
                        addURLParam("subdistrict", 2);
                String response = request(sb);
                DistrictResponse districtResponse = JSONObject.parseObject(response, DistrictResponse.class);
                for (District district : districtResponse.getDistricts()) {
                    for (District district1 : district.getDistricts()) {
                        for (District district2 : district1.getDistricts()) {
                            cityList.add(district2.getName());
                        }
                    }
                }
                redisOperator.addList(key, cityList);
                return cityList;
            }else {
                return redisOperator.getList(key);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return cityList;
    }



}
