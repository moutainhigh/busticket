package com.vpclub.bait.busticket.api.request;

import com.vpclub.bait.busticket.api.entity.InsuranceCallBackInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
@ApiModel
public class InsuranceCallBackRequest {
	 @ApiModelProperty("订单号")
     private String orderNo;
	 @ApiModelProperty("保险信息")
     private List<InsuranceCallBackInfo> insuranceInfo;
	
	@Override
	public String toString() {
		return "InsuranceCallBackBean [orderNo=" + orderNo + ", insuranceInfo="
				+ insuranceInfo + "]";
	}
}
