package com.xiaomi.rnstringweb.service;

import com.xiaomi.rnstringweb.entities.KeyInfo;

import java.util.List;

public interface KeyInfoService {
    int insert(KeyInfo keyInfo);
    int batchInsert(List<KeyInfo> keyInfoList);
    int update(KeyInfo keyInfo);
    int delete(KeyInfo keyInfo);
    KeyInfo selectByKeyName(String keyName);
    List<KeyInfo> selectByFileName(String fileName);


}
