package com.xiaomi.rnstringweb.service;

import com.xiaomi.rnstringweb.entities.KeyInfo;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ResultAnalyzeService {
    @Autowired
    private KeyInfoRepository keyInfoRepository;
    @Autowired
    private ResultAnalyzeService resultAnalyzeService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultAnalyzeService.class);
    /**
     * input map: <"en":{"key1":"value1"}>
     * output JSONObject: {"id":NULL,"keyName":"jsonKey1","en":"value1"}
     * jsonValueList:[jsonKey,jsonValue1,jsonValue12]
     */
    public List<JSONObject> pasreResultMapToJSONObject(Map<String, JSONObject> stringMap,String fileName,String product) throws JSONException {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        List<String> mapKeyList= getMapKeysList(stringMap);
        //todo 将所有的产品都给做成一个表格，有值填值，没值用N/A（没有mapKey）  or  N/A,N/A (没有jsonKey)
        List<String>dataBaseKeyList =new ArrayList<>();
        dataBaseKeyList.add("en");
        dataBaseKeyList.add("zh");
        dataBaseKeyList.add("zh_Hant");
        dataBaseKeyList.add("es");
        dataBaseKeyList.add("ru");
        dataBaseKeyList.add("fr");
        dataBaseKeyList.add("ko");
        dataBaseKeyList.add("pl");
        dataBaseKeyList.add("it");
        dataBaseKeyList.add("de");
        Set<String> allJsonKeysSet = getAllJsonKeySet(stringMap);
        for(String jsonKey:allJsonKeysSet) {
            JSONObject object = new JSONObject();
//            object.put("id", 1);
            //todo 写死的filename 和 product
            object.put("fileName",fileName);
            object.put("product",product);
            for (String mapKey : dataBaseKeyList) {
                object.put("keyName",jsonKey);
                if(!stringMap.containsKey(mapKey)){
                    object.put(mapKey,"N/A");
                }else if(stringMap.containsKey(mapKey) && stringMap.get(mapKey).isNull(jsonKey) ){
                    object.put(mapKey,"N/A,N/A");
                }
                else{
                    object.put(mapKey, stringMap.get(mapKey).getString(jsonKey));
                }
            }
            jsonObjectList.add(object);
        }
        LOGGER.info("jsonObjectList is {}",jsonObjectList);
        return jsonObjectList;
    }
    public void parseJSONObjectListToBean(List<JSONObject> jsonObjectList) throws JSONException {
//        List<KeyInfo> keyInfoList = new ArrayList<>();
        for(JSONObject jb : jsonObjectList){
            KeyInfo keyInfo = new KeyInfo();
            LOGGER.info("current jsonObject is {},in the {} place  of {} size",jb,jsonObjectList.indexOf(jb),jsonObjectList.size());
//            KeyInfo keyInfo =  JSON.parseObject(jb.toJSONString(),KeyInfo.class);
            keyInfo.setDe(jb.getString("de"));
            keyInfo.setEn(jb.getString("en"));
            keyInfo.setEs(jb.getString("es"));
            keyInfo.setFileName(jb.getString("fileName"));
            keyInfo.setFr(jb.getString("fr"));
            keyInfo.setIt(jb.getString("it"));
            keyInfo.setKo(jb.getString("ko"));
            keyInfo.setKeyName(jb.getString("keyName"));
            keyInfo.setProduct(jb.getString("product"));
            keyInfo.setPl(jb.getString("pl"));
            keyInfo.setRu(jb.getString("ru"));
            keyInfo.setZh(jb.getString("zh"));
            keyInfo.setZh_Hant(jb.getString("zh_Hant"));
//            LOGGER.info("id is {}",keyInfo.getId());
//            LOGGER.info("keyName is {}",keyInfo.getKeyName());
//            LOGGER.info("de is {}",keyInfo.getDe());
//            LOGGER.info("en is {}",keyInfo.getEn());
//            LOGGER.info("es is {}",keyInfo.getEs());
//            LOGGER.info("ko is {}",keyInfo.getKo());
//            LOGGER.info("fr is {}",keyInfo.getFr());
//            LOGGER.info("it s {}",keyInfo.getIt());
//            LOGGER.info("pl is {}",keyInfo.getPl());
//            LOGGER.info("getFileName is {}",keyInfo.getFileName());
//            LOGGER.info("getProduct is {}",keyInfo.getProduct());
//            LOGGER.info("zh is {}",keyInfo.getZh());
//            LOGGER.info("getZh_Hant is {}",keyInfo.getZh_Hant());
            LOGGER.info("keyInfo is {}",keyInfo.toString());
            keyInfoRepository.saveAndFlush(keyInfo);
        }

    }






    public List getJsonValueList(List jsonKeyList, Map<String, JSONObject> stringMap, List mapKeyList, String allOutputPath) throws JSONException, IOException {
        List<List<String>> jsonValueListOutput = new ArrayList<List<String>>();
        for (int m = 0; m < jsonKeyList.size(); m++) {
            List jsonValueList = new ArrayList();
            //TODO 对当前整个文件的数据落盘输出
            for (int n = 0; n < mapKeyList.size(); n++) {
                String jsonValue = stringMap.get(mapKeyList.get(n)).getString(jsonKeyList.get(m).toString());
                jsonValueList.add(jsonValue + ",");
            }
            //把jsonKey给设置进去，方便输出到文件
            jsonValueList.add(0, jsonKeyList.get(m));
            jsonValueListOutput.add(jsonValueList);
            LOGGER.info("jsonValueList size {},list is {}", jsonValueList.size(), jsonValueList);
        }
        return jsonValueListOutput;
    }

    //获取map<String,JSONObject>里面JSONObject所有jsonKey的并集
    public static Set<String> getAllJsonKeySet(Map<String, JSONObject> stringMap) {
        Set<String> allJsonKeysSet = new HashSet<String>();
        for (JSONObject jb : stringMap.values()) {
            Iterator iterator = jb.keys();
            while (iterator.hasNext()) {
                String jsonKey = (String) iterator.next();
                allJsonKeysSet.add(jsonKey);
            }
        }
        return allJsonKeysSet;
    }

    //获取map<String,JSONObject>里面所有mapKey
    public static List<String> getMapKeysList(Map<String, JSONObject> stringMap) {
        List mapKeyList = new ArrayList();
        for (String mapKey : stringMap.keySet()) {
            mapKeyList.add(mapKey);
        }
        return mapKeyList;
    }

    public static List<List<String>> getAllJsonLists(Map<String, JSONObject> stringMap) throws JSONException {
        Set<String> allJsonKeysSet = getAllJsonKeySet(stringMap);
        List<String> mapKeyList = getMapKeysList(stringMap);
        List<List<String>> allJsonValueLists = new ArrayList<List<String>>();
        for (String jsonKey : allJsonKeysSet) {
            List<String> jsonValueList = new ArrayList<String>();
            for (String mapKey : mapKeyList) {
                if (!stringMap.get(mapKey).isNull(jsonKey)) {
                    jsonValueList.add(stringMap.get(mapKey).getString(jsonKey));
                } else {
                    jsonValueList.add("N/A");
                }
            }
            jsonValueList.add(0, jsonKey);
            allJsonValueLists.add(jsonValueList);
        }
        return allJsonValueLists;
    }
    //主要是给文件夹localizedStrings用的
    public void outputFailResult(Map<String, HashMap<String, JSONObject>> failResultsOfAllMaps, List<Map<String, JSONObject>> mapList, String fileName, String allOutputPath, String zh, String en) throws JSONException, IOException {
        Map<String, JSONObject> failResultMap = resultAnalyzeService.getFailResult(mapList, allOutputPath, zh, en);
        if (failResultMap.size() != 0) {
            failResultsOfAllMaps.put(fileName, (HashMap<String, JSONObject>) failResultMap);
        } else {
            LOGGER.info("failResultMap is null,no need to put in failResultsOfAllMaps ");
        }
    }
    //获取结果的入口
    private Map getFailResult(List<Map<String, JSONObject>> mapList, String allOutputPath, String zh, String en) throws JSONException, IOException {
        Map<String, JSONObject> failResultMap = null;
        if (mapList.size() != 0) {
            for (int mapNum = 0; mapNum < mapList.size(); mapNum++) {
                failResultMap = new HashMap<String, JSONObject>();
                LOGGER.info("###################### 第 {} 个map,total is {} #####################", mapNum + 1, mapList.size());
                LOGGER.info("mapList.get(mapNum) is {}", mapList.get(mapNum));
                resultAnalyzeService.analyseMapResult(mapList.get(mapNum), failResultMap, allOutputPath, zh, en);
            }
        } else {
            LOGGER.info("mapList is null");
        }
        return failResultMap;
    }
    //主要用于LocalizedStrings，传入一个正常的map，输出fail的map
    private void analyseMapResult(Map<String, JSONObject> map, Map<String, JSONObject> failResultMap, String allOutputPath, String zh, String en) throws JSONException, IOException {
        List mapKeyList = new ArrayList();
        //提取map的key，作为索引
        for (String key : map.keySet()) {
            mapKeyList.add(key);
        }
        LOGGER.info("mapKeyList size is {},list is {}", mapKeyList.size(), mapKeyList);
        //校验各个JSONObject的长度都是一致的  done
        //提取一组JSONObject的key
        List jsonKeyList = new ArrayList();
        List<List<String>> jsonValueListOutput;
        JSONObject jsonObject = map.get(mapKeyList.get(0));
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String jsonKey = (String) iterator.next();
            jsonKeyList.add(jsonKey);
        }
        LOGGER.info("jsonKeyList size is {},list is {}", jsonKeyList.size(), jsonKeyList);
        //对整体按照提取粗来的mapKey和jsonKey 来获取记录的值，表达出来
        jsonValueListOutput = getJsonValueList(jsonKeyList, map, mapKeyList, allOutputPath);

        int ZH = mapKeyList.indexOf(zh);
        int EN = mapKeyList.indexOf(en);
        LOGGER.info("ZH is {},EN is {}", ZH, EN);
        getFailResultMap(jsonValueListOutput, ZH, EN, mapKeyList, failResultMap);
    }
    public void getFailResultMap(List<List<String>> jsonValueListOutput, int ZH, int EN, List mapKeyList, Map<String, JSONObject> failResultMap) throws JSONException {
//        Map<String, JSONObject> failResultMap = new HashMap<String, JSONObject>();
        for (int nc = 0; nc < jsonValueListOutput.size(); nc++) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>start to parse 第 {} 个 jsonKey，total is {}，jsonKey is {} ", nc + 1, jsonValueListOutput.size(), jsonValueListOutput.get(nc).get(0).toString());
            List jsonValueList = jsonValueListOutput.get(nc);
            //需要从第一位开始，第0位是jsonKey
            for (int jc = 1; jc < jsonValueList.size(); jc++) {
                if (jc == ZH || jc == EN) {
                    continue;
                } else {
                    if (jsonValueList.get(jc).equals(jsonValueList.get(ZH)) || jsonValueList.get(jc).equals(jsonValueList.get(EN))) {
                        JSONObject failJsonObject = new JSONObject();
                        failJsonObject.put(jsonValueList.get(0).toString(), jsonValueList.get(jc).toString());
                        //mapKeyList 比jsonValueList size少1
                        failResultMap.put(mapKeyList.get(jc - 1).toString(), failJsonObject);
                    } else {
                        LOGGER.info("good job,passed");
                    }
                }
            }
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>> parse current file finish,fail 的结果为 {}<<<<<<<<<<<<<<<<<<<<<", failResultMap);
    }



}
