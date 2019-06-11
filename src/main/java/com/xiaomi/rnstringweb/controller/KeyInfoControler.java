package com.xiaomi.rnstringweb.controller;

import com.xiaomi.rnstringweb.entities.KeyInfo;
import com.xiaomi.rnstringweb.service.KeyInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Controller
public class KeyInfoControler {
    @Autowired
    private KeyInfoRepository keyInfoRepository;

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
    @RequestMapping(value = "/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        if(file.getSize()!=0){
           //对file做处理


        }


        return "upload success!";
    }


}
