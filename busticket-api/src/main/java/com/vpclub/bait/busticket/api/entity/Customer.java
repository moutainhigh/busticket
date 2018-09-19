package com.vpclub.bait.busticket.api.entity;

import cn.vpclub.moses.utils.validator.annotations.NotEmpty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 汽车票下单乘客信息
 * @author zengbaoyou
 * @ date 2017-12-21
 */
@Data
@ApiModel("封装乘客信息")
public class Customer {
	@ApiModelProperty("乘客有效证件号码")
	@NotEmpty
	private String cardNo;		//乘客有效证件号码
	@ApiModelProperty("乘客姓名")
	@NotEmpty
	private String name;			//乘客姓名
	@ApiModelProperty("乘客手机号")
	@NotEmpty
	private String mobile;		//乘客手机号
	@ApiModelProperty("保险金额")
	private String insurancePrice;	//保险金额，如果乘客不买保险，该字段可以不传

	@Override
	public String toString() {
		return "Customer [cardNo=" + cardNo + ", name=" + name + ", mobile=" + mobile + ", insurancePrice="
				+ insurancePrice + "]";
	}
	
}
