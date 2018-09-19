package com.vpclub.bait.busticket.api.command;

import cn.vpclub.moses.core.entity.BaseEntity;
import com.vpclub.bait.busticket.api.interfaceresponse.BusTicketResponse;
import com.vpclub.bait.busticket.api.request.BusTicketRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
public class CreateBusOrderInfoCommand extends BaseEntity{
    @TargetAggregateIdentifier
    private Long id;

    @ApiModelProperty("请求参数")
    private BusTicketRequest busTicketRequest;
    @ApiModelProperty("第三方下单接口返货的部分参数")
    private BusTicketResponse busTicketResponse;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("总服务费用")
    private Double serviceAmount;
    @ApiModelProperty("总保险费")
    private Double insurAmount;
}
