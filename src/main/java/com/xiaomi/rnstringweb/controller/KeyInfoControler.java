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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
    Logger logger = LoggerFactory.getLogger(getClass());
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
    public String upload(@RequestParam(value = "file") MultipartFile file[], @RequestParam(value = "product") String product, HttpServletResponse response) throws Exception {
        Map<String, JSONObject> stringsMap = new HashMap<String, JSONObject>();
        Map<String, HashMap<String, JSONObject>> failResultsOfAllMaps = new HashMap<String, HashMap<String, JSONObject>>();
        String basePath = ExportExcelHelper.createReportDir() + "/";
        logger.info("basePath is:{}", basePath);
        String resultPath = basePath + "result.xlsx";
        logger.info("resultPath is:{}", resultPath);
        String failResultPath = basePath + "failResult.txt";
        logger.info("failResultPath is:{}", failResultPath);
        String fileName = "";
//        String product = "";
        if (file.length != 0) {
            for (int i = 0; i < file.length; i++) {
                fileName = file[i].getOriginalFilename();
                if (!fileName.endsWith("js")) {
                    continue;
                }
                logger.info("fileName is {},in the {} place of {} size", fileName, i, file.length);
                String fileStringResult = StringsHelper.parseInputStreamToString(file[i].getInputStream());
                try {
                    //设置入口，按照文件名来设置
                    Boolean flag = StringUtils.containsAny(fileName, "EN.js", "TW.js", "DE.js", "ES.js", "IT.js", "ZH.js", "FR.js", "RU.js");
                    if (flag) {
                        //空气净化器产品的入口（一个文件一个国家，不同文件的的key不一样，有多个文件）
//                    product = "空气净化器pro";
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
//                        product = "一个文件含多组base产品";
                            stringsMap = StringsHelper.parseStringBaseToMap(fileStringResult);
//                        ExportExcelHelper.exportExcel(response,fileName,stringsMap);
//                            List<JSONObject> jsonObjectList = resultAnalyzeService.pasreResultMapToJSONObject(stringsMap, fileName, product);
//                            resultAnalyzeService.parseJSONObjectListToBean(jsonObjectList);
                        } else {
                            //一个文件夹下多组产品的逻辑
//                        product = "多个文件产品";
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
                } catch (Exception e) {
                    logger.error("parse file exception",e);
                }
            }
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> all all all  all all is finished <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            //针对多个localizedStrings 下多个文件的（一个文件里面包含多个国家）
            try {
                if (failResultsOfAllMaps.size() != 0) {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(failResultPath, false));
                    BufferedWriter writer0 = new BufferedWriter(outputStreamWriter);
                    for (Map.Entry<String, HashMap<String, JSONObject>> entry : failResultsOfAllMaps.entrySet()) {
//                logger.info("file is {},fail result is {}", entry.getKey(), entry.getValue());
                        writer0.write(entry.getKey() + "," + entry.getValue());
                        writer0.newLine();
                        writer0.flush();
                    }
                } else if (stringsMap.size() != 0) {
                    ExportExcelHelper.fillExcelWithColor(stringsMap, "sheet", resultPath);
                    List<JSONObject> jsonObjectList = resultAnalyzeService.pasreResultMapToJSONObject(stringsMap, fileName, product);
                    resultAnalyzeService.parseJSONObjectListToBean(jsonObjectList);
                } else {
                    logger.error("文件类型不匹配或者不存在，请重新输入！");
                }
                File reportFile = new File(basePath);
                List<File> reportFileList = new ArrayList<>();
//            ExportExcelHelper.getAllDirsAndFiles(reportFileList,reportFile,"xlsx","txt");
                ExportExcelHelper.getAllDirsAndFiles(reportFileList, reportFile, "", "");
                ExportExcelHelper.downLoadFiles(reportFileList, response);

                return "process success!";
            } catch (Exception e) {
                logger.error("save file exception,{}",e);
                return "parse file exception "+e.getMessage()+";"+e.getCause()+e.getStackTrace();
            }
        }else{
                return ("input file is null ,try to upload again");
            }
        }


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public List<KeyInfo> getInfoByKeyName(@RequestParam(value = "keyName") String keyName) {
        List<KeyInfo> keyInfoList = keyInfoRepository.findByKeyName(keyName);
        logger.info("keyInfo", keyInfoList);
        return keyInfoList;
    }

    @RequestMapping(value = "/info/product", method = RequestMethod.GET)
    public List<KeyInfo> getInfoByKeyNameAndProduct(@RequestParam(value = "keyName") String keyName, @RequestParam(value = "product") String product) {
        List<KeyInfo> keyInfoList = keyInfoRepository.findByKeyNameAndProduct(keyName, product);
        logger.info("keyInfo {}", keyInfoList);
        return keyInfoList;
    }
    @RequestMapping(value = "/info/onlyproduct", method = RequestMethod.GET)
    public List<KeyInfo> getInfoByProduct(@RequestParam(value = "product") String product) {
        List<KeyInfo> keyInfoList = keyInfoRepository.findByProduct(product);
        logger.info("keyInfo {}", keyInfoList);
        return keyInfoList;
    }
    @RequestMapping(value = "/info/fileproduct", method = RequestMethod.GET)
    public List<KeyInfo> getInfoByFilenameAndProductNoKeyname(@RequestParam(value = "fileName") String fileName,@RequestParam(value = "product") String product) {
        List<KeyInfo> keyInfoList = keyInfoRepository.findByFileNameAndProduct(fileName,product);
        logger.info("keyInfo {}", keyInfoList);
        return keyInfoList;
    }



    @RequestMapping(value = "/info/fileName", method = RequestMethod.GET)
    public List<KeyInfo> getInfoByKeyNameAndFileName(@RequestParam(value = "keyName") String keyName, @RequestParam(value = "fileName") String fileName) {
        List<KeyInfo> keyInfoList = keyInfoRepository.findByKeyNameAndFileName(keyName, fileName);
        logger.info("keyInfo {} ", keyInfoList);
        return keyInfoList;
    }

    @RequestMapping(value = "/info/all", method = RequestMethod.GET)
    public List<KeyInfo> getInfoByAllParams(@RequestParam(value = "keyName") String keyName, @RequestParam(value = "product") String product, @RequestParam(value = "fileName") String fileName) {
        List<KeyInfo> keyInfoList = keyInfoRepository.findByKeyNameAndProductAndFileName(keyName, product, fileName);
        logger.info("keyInfo {} ", keyInfoList);
        return keyInfoList;
    }


    @RequestMapping(value = "/info/productlist", method = RequestMethod.GET)
    public List<String> getProductList() {
        List<String> productList = keyInfoRepository.findProductList();
        logger.info("productList {}", productList);
        return productList;
    }
//    @RequestMapping(value = "/info/columns", method = RequestMethod.GET)
//    public List<String> getColumnsList() {
//        List<String> columnsList = keyInfoRepository.getColumns();
//        logger.info("columnsList {}", columnsList);
//        return columnsList;
//    }


    @RequestMapping(value = "/greet1", method = RequestMethod.GET)
    public ModelAndView loginPage(@RequestParam(value = "keyName") String keyName, @RequestParam(value = "product") String productName, @RequestParam(value = "fileName") String fileName) {
//        public String index(Model model){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("greet1");
        List<KeyInfo> keyInfoList = keyInfoRepository.findByKeyNameAndProductAndFileName(keyName, productName, fileName);
        mav.addObject("info", keyInfoList);
        return mav;
//        Student students = ssi.findStudentById(201713140001);
//            model.addAttribute("s",students);
//            return "greet";
    }

    @RequestMapping(value = "/greet", method = RequestMethod.GET)
    public ModelAndView greet() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("greet");
        return mav;
    }

    @RequestMapping(value = "/greet2", method = RequestMethod.GET)
    public ModelAndView greet2() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("greet2");
        return mav;
    }

    @RequestMapping(value = "/greet5", method = RequestMethod.GET)
    public ModelAndView greet5() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("greet5");
        return mav;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView greet6() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("search");
        return mav;
    }

    @RequestMapping(value = "/search1", method = RequestMethod.GET)
    public ModelAndView greet61() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("search1");
        return mav;
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
