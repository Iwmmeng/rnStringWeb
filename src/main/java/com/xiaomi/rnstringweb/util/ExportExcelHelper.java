package com.xiaomi.rnstringweb.util;

import com.xiaomi.rnstringweb.service.ResultAnalyzeService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import static org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND;

public class ExportExcelHelper {











    public static void exportExcel(HttpServletResponse response, String fileName, Map<String, JSONObject> stringMap) throws Exception {
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "utf-8"));
        fillExcelWithColor(stringMap, fileName,response.getOutputStream());
    }

    public static void  fillExcelWithColor(Map<String, JSONObject> stringMap, String fileName, OutputStream outputStream) throws IOException, JSONException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(fileName);
        int ZH = -1;
        int EN = -1;
        //把文件名作为列表头（0行，从第一列开始）
        List<String> mapKeyList = ResultAnalyzeService.getMapKeysList(stringMap);
        List<List<String>> allJsonValueLists = ResultAnalyzeService.getAllJsonLists(stringMap);
        int count = 1;
        XSSFRow rowTitle = sheet.createRow(0);
        for (String key : mapKeyList) {
            if (key.contains("zh")) {
                ZH = mapKeyList.indexOf(key);
            }
            if (key.contains("en")) {
                EN = mapKeyList.indexOf(key);
            }
            XSSFCell cellFileName = rowTitle.createCell(count++);
            cellFileName.setCellValue(key);
        }
        int rowColloum = 1;
        for (int i = 0; i < allJsonValueLists.size(); i++) {
            XSSFRow row = sheet.createRow(rowColloum++);
            List<String> jsonValueList = allJsonValueLists.get(i);
            for (int j = 0; j < jsonValueList.size(); j++) {
                XSSFCell cellValue = row.createCell(j);
                cellValue.setCellValue(jsonValueList.get(j));
                if (ZH >= 0 && EN >= 0) {
                    if (j == ZH + 1 || j == EN + 1) {
                        continue;
                    } else {
                        if (jsonValueList.get(j).equals(jsonValueList.get(ZH + 1)) || jsonValueList.get(j).equals(jsonValueList.get(EN + 1))) {
                            XSSFCellStyle style = workbook.createCellStyle();
                            style.setFillForegroundColor((short) 40);
                            style.setFillPattern(SOLID_FOREGROUND);
                            cellValue.setCellStyle(style);
                        }
                    }
                }
            }
        }
        workbook.write(outputStream);
        workbook.close();
    }

}
