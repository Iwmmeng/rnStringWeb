package com.xiaomi.rnstringweb.entities;

import javafx.scene.input.DataFormat;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="key_info")
public class KeyInfo {
    @Id
    private String keyName;
    @Column(length = 255)
    private String deValue;
    @Column(length = 255)
    private String ruValue;
    @Column(length = 255)
    private String koValue;
    @Column(length = 255)
    private String enValue;
    @Column(length = 255)
    private String itValue;
    @Column(length = 255)
    private String frValue;
    @Column(length = 255)
    private String plValue;
    @Column(length = 255)
    private String zhValue;
    @Column(length = 255)
    private String esValue;
    @Column(length = 255)
    private String zh_HantValue;
    @Column(length = 255)
    private String fileName;
    @Column
    private Date createTime;


    public Date getCreateTime() {
        return createTime;
    }

    public KeyInfo setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
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

    public String getDeValue() {
        return deValue;
    }

    public KeyInfo setDeValue(String deValue) {
        this.deValue = deValue;
        return this;
    }

    public String getRuValue() {
        return ruValue;
    }

    public KeyInfo setRuValue(String ruValue) {
        this.ruValue = ruValue;
        return this;
    }

    public String getKoValue() {
        return koValue;
    }

    public KeyInfo setKoValue(String koValue) {
        this.koValue = koValue;
        return this;
    }

    public String getEnValue() {
        return enValue;
    }

    public KeyInfo setEnValue(String enValue) {
        this.enValue = enValue;
        return this;
    }

    public String getItValue() {
        return itValue;
    }

    public KeyInfo setItValue(String itValue) {
        this.itValue = itValue;
        return this;
    }

    public String getFrValue() {
        return frValue;
    }

    public KeyInfo setFrValue(String frValue) {
        this.frValue = frValue;
        return this;
    }

    public String getPlValue() {
        return plValue;
    }

    public KeyInfo setPlValue(String plValue) {
        this.plValue = plValue;
        return this;
    }

    public String getZhValue() {
        return zhValue;
    }

    public KeyInfo setZhValue(String zhValue) {
        this.zhValue = zhValue;
        return this;
    }

    public String getEsValue() {
        return esValue;
    }

    public KeyInfo setEsValue(String esValue) {
        this.esValue = esValue;
        return this;
    }

    public String getZh_HantValue() {
        return zh_HantValue;
    }

    public KeyInfo setZh_HantValue(String zh_HantValue) {
        this.zh_HantValue = zh_HantValue;
        return this;
    }
    @Override
    public String toString(){
        return new JSONObject(this).toString();
    }




}
