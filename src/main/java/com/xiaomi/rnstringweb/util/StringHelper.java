package com.xiaomi.rnstringweb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringHelper.class);
    public String parseInputStreamToString(InputStream inputStream) throws IOException {
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



}
