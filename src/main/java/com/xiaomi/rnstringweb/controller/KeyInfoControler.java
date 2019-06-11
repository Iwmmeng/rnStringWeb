package com.xiaomi.rnstringweb.controller;

import com.xiaomi.rnstringweb.service.KeyInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class KeyInfoControler {
    @Autowired
    private KeyInfoRepository keyInfoRepository;


//    public String getKeyInfoByKeyName(){
//        keyInfoRepository.save();
//    }


}
