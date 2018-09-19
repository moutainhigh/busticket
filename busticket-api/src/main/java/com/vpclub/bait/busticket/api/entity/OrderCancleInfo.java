package com.vpclub.bait.busticket.api.entity;

import lombok.Data;

@Data
public class OrderCancleInfo {
	private String ordercode;               //订单编号
	private String status;                  //订单状态
	private String fare;                    //退款金额

}
