package com.xiaomi.rnstringweb.util;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringsHelper {
    private static Logger logger = LoggerFactory.getLogger(StringsHelper.class);
    public static String parseInputStreamToString(InputStream inputStream) throws IOException {
        String fileStringResult = null;
        InputStreamReader isReader = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.contains("//")) {
                line = "";
            } else if (line.startsWith("import")) {
                line = "";
            } else if (line.indexOf(":") > 0 && (!line.trim().startsWith("\""))) {
                line = ("\"" + line.trim().replaceFirst(":", "\":").trim()).replace("\u00a0", "").replace("\\n", "");
            }
            sb.append(line.trim());
        }
        reader.close();
        fileStringResult = sb.toString();
        logger.info("get file StringResult: {}", fileStringResult);
        return fileStringResult;
    }

    public static List<String> getStringsList(String fileStringResult) {
        List<String> stringsList = new ArrayList<>();
        if (fileStringResult != null) {
            String stringExport = fileStringResult.substring(fileStringResult.indexOf("export"));
            String constString = StringUtils.remove(fileStringResult, stringExport);
            String[] constStringArr = constString.split("const");
            for (int i = 0; i < constStringArr.length; i++) {
                if (constStringArr[i].contains("string")) {
                    String subConst = constStringArr[i].substring(constStringArr[i].indexOf("({"), constStringArr[i].indexOf("});")).replace("({", "").trim();
                    stringsList.add(subConst);
                } else {
                    logger.info("the const str is not wanted: {}", constStringArr[i]);
                }
            }
            logger.info("stringsList is {}", stringsList);
        } else {
            logger.error("input strings is null");
        }
        return stringsList;
    }

    public static List<String> getZhHantList(String fileStringResult) {
        List<String> zhHantList = new ArrayList<>();
        if (fileStringResult != null) {
            String stringExport = fileStringResult.substring(fileStringResult.indexOf("export"));
            String constString = StringUtils.remove(fileStringResult, stringExport);
            String[] constStringArr = constString.split("const");
            for (int i = 0; i < constStringArr.length; i++) {
                if (constStringArr[i].contains("zh_Hant")) {
                    String zh_Hant = constStringArr[i].substring(constStringArr[i].indexOf("zh_Hant"), constStringArr[i].indexOf("};")).trim();
                    if (zh_Hant.endsWith(",")) {
                        StringUtils.reverse(zh_Hant).replaceFirst(",", "");
                        StringUtils.reverse(zh_Hant);
                    }
                    zh_Hant = zh_Hant + "}";
                    zhHantList.add(zh_Hant);
                } else {
                    logger.error("this const is not wanted");
                }
            }
            logger.info("zhHantList is {}", zhHantList);

        } else {
            logger.error("input strings is null");
        }
        return zhHantList;


    }
    public static Map<String,Integer> getExportString(String fileStringResult){
        Map<String,Integer> exportStringsMap = new HashMap<>();
        if (fileStringResult != null) {
            String stringExport = fileStringResult.substring(fileStringResult.indexOf("export"));
            int stringCount = StringUtils.countMatches(stringExport, "string");
            exportStringsMap.put("stringsExport", stringCount);
        }else {
            logger.error("input strings is null");
        }
        return exportStringsMap;
    }

    //对strings里面的含有大括号的结构进行处理，存到list
    public static List parseStringToList(String originStrings) {
        List foreignList = new ArrayList();
        String baseStr = null;
        String remove = null;
        if (originStrings != null) {
            for (int tmp = 0; ; tmp++) {
                if (originStrings.startsWith("\"")) {
                    baseStr = originStrings.substring(originStrings.indexOf("\""), originStrings.indexOf("},"));
                    remove = baseStr + "},";
                    //去除大括号前的逗号，确保可以转换成json窜
                    String pop = baseStr.trim();
                    if (pop.endsWith(",")) {
                        pop = StringUtils.reverse(pop).replaceFirst(",", "");
                        pop = StringUtils.reverse(pop);
                    }
                    String strPop = pop + "}";
                    originStrings = StringUtils.remove(originStrings, remove).trim();
                    foreignList.add(strPop);
//                    logger.info("leftString={}", originStrings);
                } else {
                    logger.info("stringsList is done");
                    break;
                }
            }
            logger.info("foreignList is {}", foreignList);
        } else {
            logger.info("originStrings is null");
        }
        return foreignList;
    }

    //  对strings的整体结构进行处理（包含直接引用和间接引用），存到JSONObject和map里面去
    public static Map parseStringsToMap(List foreignList, Map<String, JSONObject> stringsMap, String zhHant) throws JSONException {
        if (foreignList != null) {
            for (int m = 0; m < foreignList.size(); m++) {
//                JSONObject jsonObject = new JSONObject();
                String foreignString = foreignList.get(m).toString();
                logger.info("foreignString is {}", foreignString);
                //取地域
                String mapKey = foreignString.substring(0, foreignString.indexOf(":{")).replace("\"", "").trim();
                String valueString = foreignString.substring(foreignString.indexOf(":{")).replaceFirst(":", "").trim();
                logger.info("valueString is {}", valueString);
//                JSONObject mapObject = JSONObject.parseObject(valueString);
                JSONObject mapObject = new JSONObject(valueString);
                stringsMap.put(mapKey, mapObject);
            }
        } else {
            logger.info("input List foreignList is null");
        }
        if (zhHant != null) {
            logger.info("zhHant is {}",zhHant);
            String key = zhHant.substring(zhHant.indexOf("zh_Hant"), zhHant.indexOf("=")).replace("\"", "").trim();
            String valueString = zhHant.substring(zhHant.indexOf("{")).trim();
            JSONObject jsonObject = new JSONObject(valueString);
            stringsMap.put(key, jsonObject);
        } else {
            logger.info("input zhHant string is null");
        }
        return stringsMap;
    }

    public static List<Map<String,JSONObject>> getResultMapList(String fileStringResult) throws JSONException {
        List<Map<String,JSONObject>> mapList = new ArrayList<>();
        Map<String, Integer> exportStringsMap = StringsHelper.getExportString(fileStringResult);
        List stringsList = StringsHelper.getStringsList(fileStringResult);
        List zhHantList = StringsHelper.getZhHantList(fileStringResult);
        if (zhHantList.size() == stringsList.size() && stringsList.size() == exportStringsMap.get("stringsExport")) {
            for (int t = 0; t < stringsList.size(); t++) {
                Map<String, JSONObject> stringsMap = new HashMap();
                String strSub = stringsList.get(t).toString();
                String zhHant = zhHantList.get(t).toString();
                List foreignList = StringsHelper.parseStringToList(strSub);
                logger.info("zhHant is {}",zhHant);
                logger.info("foreignList is {}",foreignList);
                StringsHelper.parseStringsToMap(foreignList, stringsMap, zhHant);
                mapList.add(stringsMap);
            }
        } else {
            logger.error("文件的格式不对称");
        }
        return mapList;
    }
//    public static void convertStringAndZhhantToMap(List zhHantList, List stringsList, List mapList, Map<String, Integer> exportStringsMap) throws JSONException {
//        if (zhHantList.size() == stringsList.size() && stringsList.size() == exportStringsMap.get("stringsExport")) {
//            for (int t = 0; t < stringsList.size(); t++) {
//                Map<String, JSONObject> stringsMap = new HashMap();
//                String strSub = stringsList.get(t).toString();
//                String zhHant = zhHantList.get(t).toString();
//                List foreignList = StringsHelper.parseStringToList(strSub);
//                StringsHelper.parseStringsToMap(foreignList, stringsMap, zhHant);
//                mapList.add(stringsMap);
//            }
//        } else {
//            logger.error("文件的格式不对称");
//        }
//    }

    //MHLocalizableStrings 产品，对string进行处理，提取符合要求的base串，提取key提取json串
    public static Map<String, JSONObject> parseStringBaseToMap(String fileStringResult) throws JSONException {
//        List<JSONObject> baseJsonList = new ArrayList<JSONObject>();
        Map<String, JSONObject> stringMap = new HashMap<String, JSONObject>();
        String start = fileStringResult.substring(fileStringResult.indexOf("const"), fileStringResult.indexOf("export")).trim();
        String[] strArr = start.split("const ");
        for (String str : strArr) {
            if (str.contains("= {")) {
                String baseKey = str.substring(0, str.indexOf("=")).trim();
                if (StringUtils.containsAny(baseKey, "deBase", "itBase", "frBase", "ruBase", "esBase", "zhBase", "twhkBase", "enBase")) {
                    baseKey = baseKey.substring(0, baseKey.indexOf("Base"));
                    if (baseKey.contains("tw")) {
                        baseKey = "zh_Hant";
                    }
                    int begin = str.indexOf("=");
                    int end = str.indexOf("};");
                    String baseMapValue = str.substring(begin, end).replace("=", "").trim();
                    if (baseMapValue.endsWith(",")) {
                        StringUtils.removeEnd(baseMapValue, ",");
                    }
                    baseMapValue = baseMapValue + "}";
                    JSONObject baseObject = new JSONObject(baseMapValue);
                    stringMap.put(baseKey, baseObject);
                } else {
                    logger.info("current str is not match");
                }
            } else {
                logger.info("this current str is null，continue");
            }
        }
        for (Map.Entry<String, JSONObject> entry : stringMap.entrySet()) {
            logger.info("key is {},value is {}", entry.getKey(), entry.getValue());
        }
        return stringMap;
    }

    //用于提取export后面的内容，主要用于空净产品
    public static JSONObject parseStringsToJson(String fileStringResult) throws JSONException {
        if (fileStringResult != null && fileStringResult.contains("{")) {
            String start = fileStringResult.substring(fileStringResult.indexOf("{"), fileStringResult.lastIndexOf("}")).trim();
            if (start.endsWith(",")) {
                start = StringUtils.removeEnd(start, ",");
            }
            start = start + "}";
            logger.info("jsonObject from string : {}", start);
            JSONObject jsonObject = new JSONObject(start);
            //遍历这个文件的JSONObject，获取key值，存到keySet里面去
//            Iterator iterator = jsonObject.keys();
//            while (iterator.hasNext()) {
//                String jsonKey = (String) iterator.next();
////                keySet.add(jsonKey);
//            }
            return jsonObject;
        } else {
            logger.info("input string is invalid {}", fileStringResult);
        }
        return null;
    }


}
