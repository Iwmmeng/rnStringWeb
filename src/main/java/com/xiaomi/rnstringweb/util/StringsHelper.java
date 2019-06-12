package com.xiaomi.rnstringweb.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(StringsHelper.class);
    public static String parseInputStreamToString(InputStream inputStream) throws IOException {
        String fileStringResult = null;
        InputStreamReader isReader = new InputStreamReader(inputStream,"UTF-8");
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.contains("//")) {
                line = "";
            } else if (line.startsWith("import")) {
                line = "";
            } else if (line.indexOf(":") > 0 && (!line.trim().startsWith("\""))) {
                line = ("\"" + line.trim().replaceFirst(":", "\":").trim()).replace("\u00a0", "").replace("\\n","");
            }
            sb.append(line.trim());
        }
        reader.close();
        fileStringResult = sb.toString();
        LOGGER.info("get file StringResult: {}", fileStringResult);
        return fileStringResult;
    }

    //把正文文件分为strings和zh_Hant两种格式，用list存储，对于含有zhHant格式的
    public static void convertStringFileToListFile(List stringsList, List zhHantList, String fileStringResult, Map<String, Integer> exportStringsMap) {
        if (fileStringResult != null) {
            String stringExport = fileStringResult.substring(fileStringResult.indexOf("export"));
            int stringCount = StringUtils.countMatches(stringExport, "string");
            exportStringsMap.put("stringsExport", stringCount);
            String constString = StringUtils.remove(fileStringResult, stringExport);
            String[] constStringArr = constString.split("const");
            for (int i = 0; i < constStringArr.length; i++) {
                if (constStringArr[i].contains("string")) {
                    String subConst = constStringArr[i].substring(constStringArr[i].indexOf("({"), constStringArr[i].indexOf("});")).replace("({", "").trim();
                    stringsList.add(subConst);
                } else if (constStringArr[i].contains("zh_Hant")) {
                    String zh_Hant = constStringArr[i].substring(constStringArr[i].indexOf("zh_Hant"), constStringArr[i].indexOf("};")).trim();
                    if (zh_Hant.endsWith(",")) {
                        StringUtils.reverse(zh_Hant).replaceFirst(",", "");
                        StringUtils.reverse(zh_Hant);
                    }
                    zh_Hant = zh_Hant + "}";
                    zhHantList.add(zh_Hant);
                } else {
                    LOGGER.error("this const is not wanted");
                }
            }
            LOGGER.info("stringsList is {}", stringsList);
            LOGGER.info("zhHantList is {}", zhHantList);

        } else {
            LOGGER.error("input strings is null");
        }
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
//                    LOGGER.info("leftString={}", originStrings);
                } else {
                    LOGGER.info("stringsList is done");
                    break;
                }
            }
            LOGGER.info("foreignList is {}", foreignList);
        } else {
            LOGGER.info("originStrings is null");
        }
        return foreignList;
    }
    //  对strings的整体结构进行处理（包含直接引用和间接引用），存到JSONObject和map里面去
    public static Map parseStringsToMap(List foreignList, Map<String, JSONObject> stringsMap, String zhHant) throws JSONException {
        if (foreignList != null) {
            for (int m = 0; m < foreignList.size(); m++) {
//                JSONObject jsonObject = new JSONObject();
                String foreignString = foreignList.get(m).toString();
//                LOGGER.info("foreignString is {}",foreignString);
                //取地域
                String mapKey = foreignString.substring(0, foreignString.indexOf(":{")).replace("\"", "").trim();
                String valueString = foreignString.substring(foreignString.indexOf(":{")).replaceFirst(":", "").trim();
//                LOGGER.info("valueString is {}",valueString);
                JSONObject mapObject = JSONObject.parseObject(valueString);
                stringsMap.put(mapKey, mapObject);
            }
        } else {
            LOGGER.info("input List foreignList is null");
        }
        if (zhHant != null) {
            String key = zhHant.substring(zhHant.indexOf("zh_Hant"), zhHant.indexOf("=")).replace("\"", "").trim();
            String valueString = zhHant.substring(zhHant.indexOf("{")).trim();
            JSONObject jsonObject = JSONObject.parseObject(valueString);
            stringsMap.put(key, jsonObject);
        } else {
            LOGGER.info("input zhHant string is null");
        }
        return stringsMap;
    }
    public static void  convertStringAndZhhantToMap(List zhHantList, List stringsList, List mapList, Map<String, Integer> exportStringsMap) throws JSONException {
        if (zhHantList.size() == stringsList.size() && stringsList.size() == exportStringsMap.get("stringsExport")) {
            for (int t = 0; t < stringsList.size(); t++) {
                Map<String, JSONObject> stringsMap = new HashMap();
                String strSub = stringsList.get(t).toString();
                String zhHant = zhHantList.get(t).toString();
                List foreignList = StringsHelper.parseStringToList(strSub);
                StringsHelper.parseStringsToMap(foreignList, stringsMap, zhHant);
                mapList.add(stringsMap);
            }
        } else {
            LOGGER.error("文件的格式不对称");
        }
    }







}
