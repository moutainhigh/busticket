package com.vpclub.bait.busticket.api.event;

import cn.vpclub.moses.core.entity.BaseEntity;
import com.vpclub.bait.busticket.api.entity.Customer;
import com.vpclub.bait.busticket.api.interfaceresponse.BusTicketResponse;
import com.vpclub.bait.busticket.api.request.BusTicketRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("汽车票下单参数封装")
public class BusOrderInfoCreateEvent extends BaseEntity{
    private Long id;
    @ApiModelProperty("请求参数")
    private BusTicketRequest busTicketRequest;
    @ApiModelProperty("第三方下单接口返货的部分参数")
    private BusTicketResponse busTicketResponse;
    @ApiModelProperty("商城订单id（即车票订单会与商城的订单关联）")
    private String userId;

    @ApiModelProperty("总服务费用")
    private Double serviceAmount;
    @ApiModelProperty("总保险费")
    private Double insurAmount;
}
