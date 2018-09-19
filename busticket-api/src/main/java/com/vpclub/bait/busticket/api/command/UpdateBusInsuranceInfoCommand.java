package com.vpclub.bait.busticket.api.command;

import cn.vpclub.moses.core.entity.BaseEntity;
import com.vpclub.bait.busticket.api.entity.InsuranceCustomer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.Date;
import java.util.List;

@Data
public class UpdateBusInsuranceInfoCommand extends BaseEntity {
    @TargetAggregateIdentifier
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
