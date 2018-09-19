package com.vpclub.bait.busticket.api.entity;

import lombok.Data;

/**
 * 支付成功后，通知车站返回结果
 */
@Data
public class BusOrderPayInfo {
	private String amount;                                  //支付金额
	private String status;                                  //订单状态
	private String seatNo;                                  //座位号
	private String payTime;                                 //支付时间
	private String orderCode;                               //订单编号
	private String password;                                //取票密码
	@Override
	public String toString() {
		return "BusOrderPayInfo [amount=" + amount + ", status=" + status
				+ ", seatNo=" + seatNo + ", payTime=" + payTime
				+ ", orderCode=" + orderCode + ", password=" + password + "]";
	}
	
	
}
