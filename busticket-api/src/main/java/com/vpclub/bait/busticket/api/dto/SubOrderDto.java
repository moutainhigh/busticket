package com.vpclub.bait.busticket.api.dto;
import cn.vpclub.moses.utils.constant.ValidatorConditionType;
import cn.vpclub.moses.utils.validator.BaseAbstractParameter;
import cn.vpclub.moses.utils.validator.annotations.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class SubOrderDto extends BaseAbstractParameter {

//子订单id
    private String subMerchOrderNo;
//子订单金额
    private Integer payAmount;
//清结算商户编码
    private String payStoreId;
//商户编码
    private String storeId;


    //预留，可以为空值   邮费
    private Integer logisticsFee;



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
