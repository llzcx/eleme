package com.cx.springboot02.common.elasticsearch;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ElasticSearchIKWord {


    @Autowired
    RestHighLevelClient client;


    @Value("${ik.path}")
    private String path;


    /**
     * 将文本分词
     * @param text
     * @return
     */
    public List<String> getWordListByText(String text){
        try {
            List<String> list = new ArrayList<String>();
            Request request = new Request("GET", "_analyze");
            JSONObject entity = new JSONObject();
            entity.put("analyzer", "ik_max_word");
            //entity.put("analyzer", "ik_smart");
            entity.put("text", text);
            request.setJsonEntity(entity.toJSONString());
            Response response = this.client.getLowLevelClient().performRequest(request);
            JSONObject tokens = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
            JSONArray arrays = tokens.getJSONArray("tokens");
            for (int i = 0; i < arrays.size(); i++)
            {
                JSONObject obj = JSON.parseObject(arrays.getString(i));
                String token = obj.getString("token");
                list.add(token);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }


    public List<String> GetRemoteThesaurus(){
        List<String> list = new ArrayList<>();
        File file = new File(path);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String readStr;
            while ((readStr = reader.readLine()) != null) {
                if(!"".equals(readStr)){
                    list.add(readStr);
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return list;
    }

    public Boolean deleteWord(String word){
        if(word==null){
            return false;
        }
        word = word.trim();
        if(word.length()<=1){
            return false;
        }
        List<String> list = GetRemoteThesaurus();
        list.remove(word);
        return update(list);
    }

    public Boolean addWord(String word){
        if(word==null || "".equals(word)){
            return false;
        }
        word = word.trim();
        if(word.length()<=1){
            return false;
        }
        List<String> list = GetRemoteThesaurus();
        for (String s : list) {
            if(s.equals(word)){
                return false;
            }
        }
        list.add(word);
        return update(list);
    }


    private Boolean update(List<String> stringList){
        try {
            BufferedWriter bw= new BufferedWriter(new FileWriter(path));
            for (String s : stringList) {
                bw.write(s);
                bw.newLine();
            }
            bw.flush();
            bw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
