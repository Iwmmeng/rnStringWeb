package com.xiaomi.rnstringweb.service;

import com.xiaomi.rnstringweb.entities.KeyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KeyInfoRepository extends JpaRepository<KeyInfo,Integer>{

//    @Query(value = "select KeyInfo.product from KeyInfo where KeyInfo.keyName= ?1")
//     KeyInfo getKeyInfoByKeyName(String keyName);
//
//    @Override
    List<KeyInfo> findByKeyName(String keyName);
    List<KeyInfo> findByKeyNameAndProduct(String keyName,String product);
    List<KeyInfo> findByKeyNameAndProductAndFileName(String keyName,String product,String fileName);
    List<KeyInfo>findByKeyNameAndFileName(String keyName,String fileName);
    List<KeyInfo>findByProduct(String product);
    List<KeyInfo>findByFileNameAndProduct(String fileName,String product);

    @Query(value="select distinct product from KeyInfo")
    List<String> findProductList();

//    @Query(value="select COLUMN_NAME from KeyInfo")
//    List<String> getColumns();

}
