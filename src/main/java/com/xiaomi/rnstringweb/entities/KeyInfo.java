package com.xiaomi.rnstringweb.entities;

import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "key_info")
public class KeyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(length = 255, nullable = false)
    private String keyName;
    @Column(length = 255)
    private String de;
    @Column(length = 255)
    private String ru;
    @Column(length = 255)
    private String ko;
    @Column(length = 255)
    private String en;
    @Column(length = 255)
    private String it;
    @Column(length = 255)
    private String fr;
    @Column(length = 255)
    private String pl;
    @Column(length = 255)
    private String zh;
    @Column(length = 255)
    private String es;
    @Column(length = 255)
    private String zh_Hant;
    @Column(length = 255, nullable = false)
    private String fileName;
    @Column(length = 255, nullable = false)
    private String product;


    public String getProduct() {
        return product;
    }

    public KeyInfo setProduct(String product) {
        this.product = product;
        return this;
    }

//    @Column
//    private Date createTime;

    public Integer getId() {
        return id;
    }
    public String getFileName() {
        return fileName;
    }

    public KeyInfo setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getKeyName() {
        return keyName;
    }

    public KeyInfo setKeyName(String keyName) {
        this.keyName = keyName;
        return this;
    }

    public String getDe() {
        return de;
    }

    public KeyInfo setDe(String de) {
        this.de = de;
        return this;
    }

    public String getRu() {
        return ru;
    }

    public KeyInfo setRu(String ru) {
        this.ru = ru;
        return this;
    }

    public String getKo() {
        return ko;
    }

    public KeyInfo setKo(String ko) {
        this.ko = ko;
        return this;
    }

    public String getEn() {
        return en;
    }

    public KeyInfo setEn(String en) {
        this.en = en;
        return this;
    }

    public String getIt() {
        return it;
    }

    public KeyInfo setIt(String it) {
        this.it = it;
        return this;
    }

    public String getFr() {
        return fr;
    }

    public KeyInfo setFr(String fr) {
        this.fr = fr;
        return this;
    }

    public String getPl() {
        return pl;
    }

    public KeyInfo setPl(String pl) {
        this.pl = pl;
        return this;
    }

    public String getZh() {
        return zh;
    }

    public KeyInfo setZh(String zh) {
        this.zh = zh;
        return this;
    }

    public String getEs() {
        return es;
    }

    public KeyInfo setEs(String es) {
        this.es = es;
        return this;
    }

    public String getZh_Hant() {
        return zh_Hant;
    }

    public KeyInfo setZh_Hant(String zh_Hant) {
        this.zh_Hant = zh_Hant;
        return this;
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }


}
