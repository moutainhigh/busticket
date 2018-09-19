package com.vpclub.bait.busticket.command.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pay.config")
public class PayConfig {
    private String requestUrl;
    private String payMethod;
    private String refundMethod;
    private String backNotifyUrl;
    private String frontUrl;
    private String version;
    private String appID;
    private String storeId;
    private String payStoreId;
    private Double serviceRate;
    private String refundNoticeUrl;
    private Boolean devMode;

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getRefundMethod() {
        return refundMethod;
    }

    public void setRefundMethod(String refundMethod) {
        this.refundMethod = refundMethod;
    }

    public String getBackNotifyUrl() {
        return backNotifyUrl;
    }

    public void setBackNotifyUrl(String backNotifyUrl) {
        this.backNotifyUrl = backNotifyUrl;
    }

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getPayStoreId() {
        return payStoreId;
    }

    public void setPayStoreId(String payStoreId) {
        this.payStoreId = payStoreId;
    }

    public Double getServiceRate() {
        return serviceRate;
    }

    public void setServiceRate(Double serviceRate) {
        this.serviceRate = serviceRate;
    }

    public String getRefundNoticeUrl() {
        return refundNoticeUrl;
    }

    public void setRefundNoticeUrl(String refundNoticeUrl) {
        this.refundNoticeUrl = refundNoticeUrl;
    }

    public Boolean getDevMode() {
        return devMode;
    }

    public void setDevMode(Boolean devMode) {
        this.devMode = devMode;
    }
}
