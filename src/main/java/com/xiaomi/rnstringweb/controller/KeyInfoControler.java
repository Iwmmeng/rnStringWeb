package com.xiaomi.rnstringweb.controller;

import com.xiaomi.rnstringweb.entities.KeyInfo;
import com.xiaomi.rnstringweb.service.KeyInfoRepository;
import com.xiaomi.rnstringweb.service.ResultAnalyzeService;
import com.xiaomi.rnstringweb.util.ExportExcelHelper;
import com.xiaomi.rnstringweb.util.StringsHelper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
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

    //todo 1. 每次上传的都是同一产品的文件 2.product 的命名  3.HttpServletResponse response 这个要怎么使用？？？
    //上传上来的文件，做处理
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam(value = "file") MultipartFile file[], HttpServletResponse response) throws Exception {
        Map<String, JSONObject> stringsMap = new HashMap<String, JSONObject>();
        Map<String, HashMap<String, JSONObject>> failResultsOfAllMaps = new HashMap<String, HashMap<String, JSONObject>>();
        String basePath = ExportExcelHelper.createReportDir()+"/";

        System.out.println("basePath"+basePath);
        String resultPath = basePath + "result.xlsx";
        System.out.println(resultPath);
        String failResultPath = basePath + "failResult.txt";
        System.out.println(failResultPath);
        String fileName = "";
        String product = "";
        if (file.length != 0) {
            for (int i = 0; i < file.length; i++) {
                fileName = file[i].getOriginalFilename();
                LOGGER.info("fileName is {},in the {} place of {} size", fileName, i, file.length);
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
                } else {
                    if (fileName.contains("String")) {
                        //MHLocalizableString产品，一个文件里面有多组base，且每组的key都不太一致
                        product = "一个文件含多组base产品";
                        stringsMap = StringsHelper.parseStringBaseToMap(fileStringResult);
//                        ExportExcelHelper.exportExcel(response,fileName,stringsMap);
                        List<JSONObject> jsonObjectList = resultAnalyzeService.pasreResultMapToJSONObject(stringsMap, fileName, product);
                        resultAnalyzeService.parseJSONObjectListToBean(jsonObjectList);
                    } else {
                        //一个文件夹下多组产品的逻辑
                        product = "多个文件产品";
                        String zh = "zh";
                        String en = "en";
                        String outputPath = (basePath + fileName.replace(".js", ""));
                        List<Map<String, JSONObject>> mapList = StringsHelper.getResultMapList(fileStringResult);
                        //落盘所有普通数据
                        if (mapList.size() > 1) {
                            for (int k = 0; k < mapList.size(); k++) {
                                Map<String, JSONObject> map = mapList.get(k);
                                outputPath = outputPath + k + ".xlsx";
                                ExportExcelHelper.fillExcelWithColor(map, fileName, outputPath);
                                outputPath = basePath + fileName.replace(".js", "");
                            }
                        } else {
                            outputPath += ".xlsx";
                            ExportExcelHelper.fillExcelWithColor(mapList.get(0), fileName, outputPath);
                        }
                        resultAnalyzeService.outputFailResult(failResultsOfAllMaps, mapList, fileName, outputPath, zh, en);
                        for (Map<String, JSONObject> stringMap : mapList) {
                            List<JSONObject> jsonObjectList = resultAnalyzeService.pasreResultMapToJSONObject(stringMap, fileName, product);
                            resultAnalyzeService.parseJSONObjectListToBean(jsonObjectList);
                        }
                    }
                }
            }
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> all all all  all all is finished <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            //针对多个localizedStrings 下多个文件的（一个文件里面包含多个国家）
            if (failResultsOfAllMaps.size() != 0) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(failResultPath, false));
                BufferedWriter writer0 = new BufferedWriter(outputStreamWriter);
                for (Map.Entry<String, HashMap<String, JSONObject>> entry : failResultsOfAllMaps.entrySet()) {
//                LOGGER.info("file is {},fail result is {}", entry.getKey(), entry.getValue());
                    writer0.write(entry.getKey() + "," + entry.getValue());
                    writer0.newLine();
                    writer0.flush();
                }
            } else if (stringsMap.size() != 0) {
                ExportExcelHelper.fillExcelWithColor(stringsMap, "sheet", resultPath);
                List<JSONObject> jsonObjectList = resultAnalyzeService.pasreResultMapToJSONObject(stringsMap, fileName, product);
                resultAnalyzeService.parseJSONObjectListToBean(jsonObjectList);
            } else {
                LOGGER.error("文件不存在，请重新输入！");
            }
            File reportFile =  new File(basePath);
            List<File> reportFileList = new ArrayList<>();
            ExportExcelHelper.getAllDirsAndFiles(reportFileList,reportFile,"xlsx","txt");
            ExportExcelHelper.downLoadFiles(reportFileList,response);

            return "process success!";
        } else {
            return ("input file is null ,try to upload again");
        }
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public List<KeyInfo> getInfoByKeyName(@RequestParam(value = "keyName") String keyName) {
        List<KeyInfo> keyInfoList = keyInfoRepository.findByKeyName(keyName);
        LOGGER.info("keyInfo", keyInfoList);
        return keyInfoList;
    }

    @RequestMapping(value = "/info/product", method = RequestMethod.GET)
    public List<KeyInfo> getInfoByKeyName(@RequestParam(value = "keyName") String keyName, @RequestParam(value = "product") String product) {
        List<KeyInfo> keyInfoList = keyInfoRepository.findByKeyNameAndProduct(keyName, product);
        LOGGER.info("keyInfo", keyInfoList);
        return keyInfoList;
    }

    @Test
    public void getPath() throws FileNotFoundException {
        //获取跟目录
//        File path = new File(ResourceUtils.getURL("classpath:").getPath());
//        if(!path.exists()) path = new File("");
//        System.out.println("path:"+path.getAbsolutePath());
        String basePath = new ApplicationHome(this.getClass()).getSource().getParentFile().getPath() + "/files/";
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        System.out.println(basePath);

    }

}
