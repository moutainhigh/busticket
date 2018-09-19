package com.vpclub.bait.busticket.api.request;


import cn.vpclub.moses.utils.validator.BaseAbstractParameter;
import com.vpclub.bait.busticket.api.dto.SubOrderDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
public class PayRequest extends BaseAbstractParameter{

    @ApiModelProperty(value = "订单id")
    private String orderNo;

    @ApiModelProperty(value = "订单描述信息")
    private String orderDesc;

    @ApiModelProperty(value="订单金额")
    private Integer totalAmount;

    @ApiModelProperty(value="子订单")
    private List<SubOrderDto> subList;

    @ApiModelProperty(value="用户号码")
    private String telphone;

    @ApiModelProperty(value="用户Id")
    private String userId;

    @ApiModelProperty(value="订单状态修改回调URL")
    private String backNotifyUrl;

    @ApiModelProperty(value="支付成页面URL，透传至清结算")
    private String frontUrl;

    @ApiModelProperty(value="appId")
    private String appId;

    //默认值 1.0
    @ApiModelProperty(value="支付版本")
    private String version;

    @ApiModelProperty(value="签名")
    private String sign;

    private String devMode;

    private String attach1;

    private String attach2;

    public String getDevMode() {
        return devMode==null?"":devMode;
    }

    public String getAttach1() {
        return attach1==null?"":attach1;
    }

    public String getAttach2() {
        return attach2==null?"":attach2;
    }

    //和包电子券商户编号,若该字段不为空，那么支付报文中的ticketpayflag为2，paycnl为CMPAY，paydirect为0
    private String ticketAccount;

    private String reserve1;

    private String reserve2;

    private String reserve3;

    private String reserve4;

    private String reserve5;

    private String reserve6;

    private String reserve7;

    private String reserve8;

    private String reserve9;

    private String reserve10;
}
