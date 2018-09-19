package com.vpclub.bait.busticket.api.entity;

import lombok.Data;

@Data
public class BusTicketOrderCode {

	private String orderCode;
	private String orderId;
	
	@Override
	public String toString() {
		return "BusTicketOrderCode [orderCode=" + orderCode + ", orderId="
				+ orderId + "]";
	}
	
	
}
