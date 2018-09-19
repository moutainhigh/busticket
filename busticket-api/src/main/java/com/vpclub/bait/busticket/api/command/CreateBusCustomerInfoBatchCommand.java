package com.vpclub.bait.busticket.api.command;


import cn.vpclub.moses.core.entity.BaseEntity;
import com.vpclub.bait.busticket.api.entity.Customer;
import com.vpclub.bait.busticket.api.entity.InsuranceCustomer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.List;

@Data
public class CreateBusCustomerInfoBatchCommand extends BaseEntity {
    @TargetAggregateIdentifier
    private Long id;

    private List<Customer> customers;
    private String orderCode;
}
