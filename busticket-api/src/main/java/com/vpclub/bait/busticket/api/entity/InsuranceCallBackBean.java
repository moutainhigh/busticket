package com.vpclub.bait.busticket.api.entity;

import java.util.List;

/**
 *
 * @author  zengbaoyou
 * @E-mail:13678717317@139.com
 * @date 创建时间：2017年12月20日 上午17:25:40
 */
public class InsuranceCallBackBean {
        
     private String orderNo;
     private List<InsuranceCallBackInfo> insuranceInfo;
	
     public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public List<InsuranceCallBackInfo> getInsuranceInfo() {
		return insuranceInfo;
	}
	public void setInsuranceInfo(List<InsuranceCallBackInfo> insuranceInfo) {
		this.insuranceInfo = insuranceInfo;
	}
	@Override
	public String toString() {
		return "InsuranceCallBackBean [orderNo=" + orderNo + ", insuranceInfo="
				+ insuranceInfo + "]";
	}
}
