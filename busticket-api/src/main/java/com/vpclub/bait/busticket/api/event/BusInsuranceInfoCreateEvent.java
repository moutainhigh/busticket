package com.vpclub.bait.busticket.api.event;

import cn.vpclub.moses.core.entity.BaseEntity;
import com.vpclub.bait.busticket.api.entity.InsuranceCustomer;
import com.vpclub.bait.busticket.api.interfaceresponse.BusTicketResponse;
import com.vpclub.bait.busticket.api.request.BusTicketRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("保险参数")
public class BusInsuranceInfoCreateEvent extends BaseEntity{
    private Long id;
    @ApiModelProperty("下单保险客户")
    private List<InsuranceCustomer> insuranceCustomers;
    @ApiModelProperty("价格")
    private Double price;
    @ApiModelProperty("订单编号")
    private String orderCode;
}
