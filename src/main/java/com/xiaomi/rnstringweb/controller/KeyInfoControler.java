package com.xiaomi.rnstringweb.controller;

import com.xiaomi.rnstringweb.entities.KeyInfo;
import com.xiaomi.rnstringweb.service.ExcelService;
import com.xiaomi.rnstringweb.service.KeyInfoRepository;
import com.xiaomi.rnstringweb.service.ResultAnalyzeService;
import com.xiaomi.rnstringweb.util.StringsHelper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class KeyInfoControler {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyInfoControler.class);
    @Autowired
    private KeyInfoRepository keyInfoRepository;
    @Autowired
    private ResultAnalyzeService resultAnalyzeService;

    @GetMapping(value = "saveKeyInfo")
    public String saveKeyInfo() {
        KeyInfo keyInfo = new KeyInfo();

        keyInfoRepository.save(keyInfo);
        return "success";
    }

    @RequestMapping("/index")
    public String getIndex() {
        return "index";
    }

    //todo 1. 每次上传的都是同一产品的文件 2.product 的命名
    //上传上来的文件，做处理
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam(value = "file") MultipartFile file[]) throws IOException, JSONException {
        Map<String, JSONObject> stringsMap = new HashMap<String, JSONObject>();
        String fileName="";
        String product="";
        if (file.length != 0) {
            for (int i = 0; i < file.length; i++) {
                 fileName = file[i].getOriginalFilename();
                LOGGER.info("fileName is {},in the {} place of {} size", fileName,i,file.length);
                String fileStringResult = StringsHelper.parseInputStreamToString(file[i].getInputStream());
                //设置入口，按照文件名来设置
                Boolean flag = StringUtils.containsAny(fileName, "EN.js", "TW.js", "DE.js", "ES.js", "IT.js", "ZH.js", "FR.js", "RU.js");
                if (flag) {
                    //空气净化器产品的入口（一个文件一个国家，不同文件的的key不一样，有多个文件）
                     product = "空气净化器pro";
                    String countryCode;
                    if (fileName.contains("TW")) {
                        countryCode = "zh_Hant";
                    } else {
                        countryCode = fileName.substring(fileName.lastIndexOf("-"), fileName.indexOf(".js")).replace("-", "").toLowerCase();
                    }
                    JSONObject jb = StringsHelper.parseStringsToJson(fileStringResult);
                    stringsMap.put(countryCode, jb);
                }else {
                    if(fileName.contains("String")){
                        //一个文件里面有多组base，且每组的key都不太一致
                         product = "一个文件含多组base产品";
                        stringsMap = StringsHelper.parseStringBaseToMap(fileStringResult);




                        List<JSONObject> jsonObjectList = resultAnalyzeService.pasreResultMapToJSONObject(stringsMap, fileName, product);
                        resultAnalyzeService.parseJSONObjectListToBean(jsonObjectList);
                    }else {
                        //文件下多组的逻辑
                         product = "多个文件产品";
                        Map<String, Integer> exportStringsMap = new HashMap();
                        exportStringsMap.put("stringsExport", 0);
                        List stringsList = new ArrayList();
                        List zhHantList = new ArrayList();
                        List<Map<String, JSONObject>> mapList = new ArrayList();
                        String zh = "zh";
                        String en = "en";
                        StringsHelper.convertStringFileToListFile(stringsList, zhHantList, fileStringResult, exportStringsMap);
                        StringsHelper.convertStringAndZhhantToMap(zhHantList, stringsList, mapList, exportStringsMap);
                        for (Map<String, JSONObject> stringMap : mapList) {
                            List<JSONObject> jsonObjectList = resultAnalyzeService.pasreResultMapToJSONObject(stringMap, fileName, product);
                            resultAnalyzeService.parseJSONObjectListToBean(jsonObjectList);
                        }
                    }
                }
            }
            if(stringsMap.size()!=0){
                List<JSONObject> jsonObjectList = resultAnalyzeService.pasreResultMapToJSONObject(stringsMap, fileName, product);
                resultAnalyzeService.parseJSONObjectListToBean(jsonObjectList);
            }
            return "upload success!";
        } else {
            return "input file is null ,try to upload again";
        }
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public List<KeyInfo> getInfoByKeyName(@RequestParam(value = "keyName") String keyName){
        List<KeyInfo> keyInfoList = keyInfoRepository.findByKeyName(keyName);

        LOGGER.info("keyInfo",keyInfoList);
        return keyInfoList;
    }
    @RequestMapping(value = "/info/product", method = RequestMethod.GET)
    public List<KeyInfo> getInfoByKeyName(@RequestParam(value = "keyName") String keyName,@RequestParam(value = "product") String product){
        List<KeyInfo> keyInfoList = keyInfoRepository.findByKeyNameAndProduct(keyName,product);
        LOGGER.info("keyInfo",keyInfoList);
        return keyInfoList;
    }





}
