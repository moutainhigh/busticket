package com.vpclub.bait.busticket.api.entity;

import lombok.Data;

import java.util.List;

/**
 *
 * @author  zengbaoyou
 * @E-mail:13678717317@139.com
 * @date 创建时间：2017年12月20日 上午17:25:56
 */
@Data
public class BusOrderDetailInfo {
	private List<BusTicketOrder> busOrders;            //车票订单详情列表

	@Override
	public String toString() {
		return "BusOrderDetailInfo [busOrders=" + busOrders + "]";
	}
	
	
	
	
}
