package com.vpclub.bait.busticket.api.event;

import cn.vpclub.moses.core.entity.BaseEntity;
import com.vpclub.bait.busticket.api.entity.InsuranceCustomer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("保险修改事件")
public class BusInsuranceInfoUpdateEvent extends BaseEntity{
    private Long id;
    private String orderNo;
    private String cardNo;
    private int status;
    private String policyNo;
    private String company;
    private Date termDate;
    private Date fromDate;
    private Date time;
}
