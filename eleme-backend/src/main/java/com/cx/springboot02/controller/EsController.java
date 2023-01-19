package com.cx.springboot02.controller;


import com.cx.springboot02.common.E.AuthorizeType;
import com.cx.springboot02.common.E.ResultCode;
import com.cx.springboot02.common.JsonResult;
import com.cx.springboot02.common.ResultTool;
import com.cx.springboot02.common.elasticsearch.ElasticSearchIKWord;
import com.cx.springboot02.common.utils.PreAuthorize;
import com.cx.springboot02.common.utils.Unobstructed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/es")
@PreAuthorize(values = {AuthorizeType.CUSTOMER})
@Slf4j
public class EsController {


    @Autowired
    ElasticSearchIKWord elasticSearchIKWord;

    /**
     * 删除词条
     * @param str
     * @return
     */
    @DeleteMapping("/{str}")
    @Unobstructed
    public JsonResult<Object> delete(@PathVariable String str){
        if(elasticSearchIKWord.deleteWord(str)){
            return ResultTool.success(null);
        }else{
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }
    }


    /**
     * 获取远程词库列表
     * @return
     */
    @GetMapping("/list")
    @Unobstructed
    public JsonResult<Object> get(){
        return ResultTool.success(elasticSearchIKWord.GetRemoteThesaurus());
    }


    /**
     * 添加词条
     * @param str
     * @return
     */
    @PostMapping("/{str}")
    @Unobstructed
    public JsonResult<Object> add(@PathVariable String str){
        if(elasticSearchIKWord.addWord(str)){
            return ResultTool.success(null);
        }else{
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }
    }





}
