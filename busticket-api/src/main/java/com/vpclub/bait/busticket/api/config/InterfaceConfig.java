package com.vpclub.bait.busticket.api.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.bus")
@ApiModel("汽车票第三方接口信息封装")
public class InterfaceConfig {
    @ApiModelProperty("是否是测试环境")
    //1代表测试或本地，2代表prod或者stage
    private int isTest;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("秘钥")
    private String key;
    @ApiModelProperty("秘钥文件路径")
    private String keyFilepath;
    @ApiModelProperty("主机")
    private String host;
    @ApiModelProperty("获取token的路径")
    private String session;
    @ApiModelProperty("获取站点的路径")
    private String station;
    @ApiModelProperty("获取起始站点")
    private String origin;
    @ApiModelProperty("下单路径")
    private String order;
    @ApiModelProperty("获取班次路径")
    private String classes;
    @ApiModelProperty("回调地址")
    private String backUrl;
    private String desKey;

    public int getIsTest() {
        return isTest;
    }

    public void setIsTest(int isTest) {
        this.isTest = isTest;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyFilepath() {
        return keyFilepath;
    }

    public void setKeyFilepath(String keyFilepath) {
        this.keyFilepath = keyFilepath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getDesKey() {
        return desKey;
    }

    public void setDesKey(String desKey) {
        this.desKey = desKey;
    }
}
