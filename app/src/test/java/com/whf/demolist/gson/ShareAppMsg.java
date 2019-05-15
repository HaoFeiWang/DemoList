package com.whf.demolist.gson;


import java.util.Arrays;

/**
 * 分享程序跳转消息
 * Created by @author WangHaoFei on 2018/9/20.
 */

public class ShareAppMsg {

    private String desc;
    private String appName;
    private String targetPackage;
    private String targetClass;
    private String extInfo;
    private String action;
    private String transaction;
    private String packageName;

    private byte[] haha;
    private String hehe1;
    private String hehe2;
    private String hehe3;
    private String hehe4;
    private String hehe5;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public byte[] getHaha() {
        return haha;
    }

    public void setHaha(byte[] haha) {
        this.haha = haha;
    }

    public String getHehe1() {
        return hehe1;
    }

    public void setHehe1(String hehe1) {
        this.hehe1 = hehe1;
    }

    public String getHehe2() {
        return hehe2;
    }

    public void setHehe2(String hehe2) {
        this.hehe2 = hehe2;
    }

    public String getHehe3() {
        return hehe3;
    }

    public void setHehe3(String hehe3) {
        this.hehe3 = hehe3;
    }

    public String getHehe4() {
        return hehe4;
    }

    public void setHehe4(String hehe4) {
        this.hehe4 = hehe4;
    }

    public String getHehe5() {
        return hehe5;
    }

    public void setHehe5(String hehe5) {
        this.hehe5 = hehe5;
    }

    @Override
    public String toString() {
        return "ShareAppMsg{" +
                "desc='" + desc + '\'' +
                ", appName='" + appName + '\'' +
                ", targetPackage='" + targetPackage + '\'' +
                ", targetClass='" + targetClass + '\'' +
                ", extInfo='" + extInfo + '\'' +
                ", action='" + action + '\'' +
                ", transaction='" + transaction + '\'' +
                ", packageName='" + packageName + '\'' +
                ", haha=" + Arrays.toString(haha) +
                ", hehe1='" + hehe1 + '\'' +
                ", hehe2='" + hehe2 + '\'' +
                ", hehe3='" + hehe3 + '\'' +
                ", hehe4='" + hehe4 + '\'' +
                ", hehe5='" + hehe5 + '\'' +
                '}';
    }
}
