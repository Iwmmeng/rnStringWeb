package com.xiaomi.rnstringweb.util;

import com.xiaomi.rnstringweb.service.ResultAnalyzeService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND;

public class ExportExcelHelper {

//    public static void exportExcel(HttpServletResponse response, String fileName, Map<String, JSONObject> stringMap) throws Exception {
//        // 告诉浏览器用什么软件可以打开此文件
//        response.setHeader("content-Type", "application/vnd.ms-excel");
//        // 下载文件的默认名称
//        response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "utf-8"));
//        fillExcelWithColor(stringMap, fileName,response.getOutputStream());
//    }

    public static void  fillExcelWithColor(Map<String, JSONObject> stringMap, String fileName, String outPath) throws IOException, JSONException {
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
        workbook.write(new FileOutputStream(outPath));
        workbook.close();
    }


    public static List<File> getAllDirsAndFiles(List fileList, File file,String filter1,String filter2) {
        if (file.exists() && file.isFile()) {
            if (file.getName().endsWith(filter1) || file.getName().endsWith(filter2)) {
                fileList.add(file);
            }
        } else {
            for (File sub : file.listFiles()) {
                getAllDirsAndFiles(fileList, sub,filter1,filter2);
            }
        }
        return fileList;
    }
    public static HttpServletResponse downLoadFiles(List<File> files, HttpServletResponse response) throws Exception {
        try {
            // List<File> 作为参数传进来，就是把多个文件的路径放到一个list里面
            // 创建一个临时压缩文件
            // 临时文件可以放在CDEF盘中，但不建议这么做，因为需要先设置磁盘的访问权限，最好是放在服务器上，方法最后有删除临时文件的步骤
//            String zipFilename = "D:/tempFile.zip";
            String zipFilename = "/Users/huamiumiu/Miot/workCode/rnStringWeb/target/classes/static/report/tempFile.zip";
            File file = new File(zipFilename);
            file.createNewFile();
//            if (!file.exists()) {
//                file.createNewFile();
//            }else {
//                file.delete();
//                file.createNewFile();
//            }
            response.reset();
            // response.getWriter()
            // 创建文件输出流
            FileOutputStream fous = new FileOutputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(fous);
            zipFile(files, zipOut);
            zipOut.close();
            fous.close();
            return downloadZip(file, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    public static void zipFile(List files, ZipOutputStream outputStream) {
        int size = files.size();
        for (int i = 0; i < size; i++) {
            File file = (File) files.get(i);
            zipFile(file, outputStream);
        }
    }


    public static void zipFile(File inputFile, ZipOutputStream outputStream) {
        try {
            if (inputFile.exists()) {
                if (inputFile.isFile()) {
                    FileInputStream IN = new FileInputStream(inputFile);
                    BufferedInputStream bins = new BufferedInputStream(IN, 512);
                    ZipEntry entry = new ZipEntry(inputFile.getName());
                    outputStream.putNextEntry(entry);
                    // 向压缩文件中输出数据
                    int nNumber;
                    byte[] buffer = new byte[512];
                    while ((nNumber = bins.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, nNumber);
                    }
                    // 关闭创建的流对象
                    bins.close();
                    IN.close();
                } else {
                    try {
                        File[] files = inputFile.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            zipFile(files[i], outputStream);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpServletResponse downloadZip(File file, HttpServletResponse response) {
        if (!file.exists()) {
            System.out.println("待压缩的文件目录：" + file + "不存在.");
        } else {
            try {
                // 以流的形式下载文件。
                System.out.println("file is "+file);
                InputStream fis = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                // 清空response
                response.reset();

                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");

                // 如果输出的是中文名的文件，在此处就要用URLEncoder.encode方法进行处理，+ new String(file.getName().getBytes("GB2312"), "ISO8859-1")
                response.setHeader("Content-Disposition",
                        "attachment;filename=tempFile.zip" );
                toClient.write(buffer);
                toClient.flush();
                toClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
//                    File f = new File(file.getPath());
//                    f.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
    public static String createReportDir() throws FileNotFoundException {
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if (!path.exists()) {
            path = new File("");
        }
        File report = new File(path.getAbsolutePath(), "static/report/");
        if (!report.exists()) {
            report.mkdirs();
        }else{
            report.delete();
            report.mkdirs();
        }
        return report.getAbsolutePath();
    }

    @RequestMapping("/downlo")
    public void downLoad(HttpServletResponse response) {
        String filename = "劳动合同.doc";
        String filePath = "C:\\Users\\Liang Qizhao\\Desktop\\";
        File file = new File(filePath + "/" + filename);
        if (file.exists()) { //判断文件父目录是否存在
            try {
                filename = java.net.URLEncoder.encode(filename, "UTF-8");
                filename = new String(filename.getBytes(), "iso-8859-1");
                response.setContentType("application/force-download");
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Content-Disposition", "attachment;fileName=" + filename);

                byte[] buffer = new byte[1024];
                FileInputStream fis = null; //文件输入流
                BufferedInputStream bis = null;

                OutputStream os = null; //输出流

                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer);
                    i = bis.read(buffer);
                }
                bis.close();
                fis.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("----------file download" + filename);
        }
    }













}
