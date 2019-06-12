package com.xiaomi.rnstringweb.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xiaomi.rnstringweb.entities.KeyInfo;
import com.xiaomi.rnstringweb.service.KeyInfoRepository;
import com.xiaomi.rnstringweb.service.ResultAnalyzeService;
import com.xiaomi.rnstringweb.util.StringsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class KeyInfoControler {
    @Autowired
    private KeyInfoRepository keyInfoRepository;
    @Autowired
    private ResultAnalyzeService resultAnalyzeService;

    @GetMapping(value = "saveKeyInfo")
    public String saveKeyInfo(){
        KeyInfo keyInfo = new KeyInfo();

        keyInfoRepository.save(keyInfo);
        return "success";
    }

    @RequestMapping("/index")
    public String getIndex(){
        return "index";
    }

    //上传上来的文件，做处理
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String upload(@RequestParam(value = "file") MultipartFile file) throws IOException, JSONException {
        if(file.getSize()!=0){
           //对file做处理
            //todo 先不考虑处理上传文件的类型，
            String fileStringResult = StringsHelper.parseInputStreamToString(file.getInputStream());
            Map<String, Integer> exportStringsMap = new HashMap();
            exportStringsMap.put("stringsExport", 0);
            List stringsList = new ArrayList();
            List zhHantList = new ArrayList();
            List<Map<String, JSONObject>> mapList = new ArrayList();
            String zh = "zh";
            String en = "en";
            StringsHelper.convertStringFileToListFile(stringsList, zhHantList, fileStringResult, exportStringsMap);
            StringsHelper.convertStringAndZhhantToMap(zhHantList, stringsList, mapList, exportStringsMap);
            for(Map<String,JSONObject>stringMap: mapList) {
               List<JSONObject>jsonObjectList = resultAnalyzeService.pasreResultMapToJSONObject(stringMap);
                resultAnalyzeService.parseJSONObjectListToBean(jsonObjectList);
            }



        }


        return "upload success!";
    }


}
