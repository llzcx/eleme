package com.cx.springboot02.common.redis.HotKeyCache;


import com.cx.springboot02.common.elasticsearch.ElasticSearchIKWord;
import com.cx.springboot02.common.redis.RedisFinal;
import com.cx.springboot02.common.redis.RedisOperator;
import com.cx.springboot02.service.impl.GaoDeMapServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class HotKeyCache {

    private final String prefix  = RedisFinal.ELEME + RedisFinal.HOT_KEY;

    @Autowired
    GaoDeMapServiceImpl gaoDeMapService;

    @Autowired
    ElasticSearchIKWord elasticSearchIKWord;

    @Autowired
    RedisOperator redisOperator;


    /**
     * 将字符串拆分成不同长度的单词 如果单词长度超过2则记为词语 并设置为热搜
     * @param key
     * @param geohash
     * @return
     */
    public Boolean SaveHotWord(String key,String geohash){
        try {
            String cityName = gaoDeMapService.city(geohash);
            List<String> wordListByText = elasticSearchIKWord.getWordListByText(key);
            for (String word : wordListByText) {
                //如果长度大于等于2才别系统识别为词
                if (word.length()>=2){
                    //查是否存在
                    if (redisOperator.checkZsetValueIsExist(prefix+cityName,word)){

                    }else{

                    }
                    //自增
                    redisOperator.incrementScore(prefix+cityName, word,1);
                    //查询score是否大于256次 大于256次说明为热key 时间设置长一些

                }
            }
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public List<ZSetData> getHotWordList(String geohash){
        try {
            String cityName = gaoDeMapService.city(geohash);
            String key = prefix + cityName;
            Set<Object> objects = redisOperator.reverseRange(key, 0, 9);
            List<ZSetData> zSetData = new ArrayList<>();
            for (Object object : objects) {
                ZSetData temp = new ZSetData();
                temp.setWord((String) object);
                temp.setNum((int)redisOperator.getScore(key, object));
                zSetData.add(temp);
            }
            return zSetData;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return  null;
    }

    public Boolean deleteInfoByCity(String city){
        return redisOperator.del(prefix+city);
    }


    /**
     * 清除缓存数据 返回删除的数量
     */
    public int clearHotKeyCache(){
        int num = 0;
        List<String> cityList = gaoDeMapService.getCityList();
        for (String cityName : cityList) {
            String key = prefix + cityName;
            Set<String> values = redisOperator.range(key, 99, -1);
            for (String value: values) {
                redisOperator.remove(key,value);
                num++;
            }
        }
        return num;
    }
}
