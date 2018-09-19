package com.vpclub.bait.busticket.api.entity;

import lombok.Data;

/**
 *
 * @author  zengbaoyou
 * @E-mail:13678717317@139.com
 * @date 创建时间：2017年12月20日 上午17:25:40
 */
@Data
public class InsuranceInfo {
	private String cardNo;	//承保人证件号
	private Integer price;
	private String status;
	private String name;
	private String company;
	private String mobile;
	private String time;	//投保时间
	private String termDate;	//保险生效起始时间（投保成功才返回）
	private String fromDate;	//保险生效结束时间（投保成功才返回）
	private String policyNo;	//保单号（投保成功才返回）

	@Override
	public String toString() {
		return "InsuranceInfo [cardNo=" + cardNo + ", price=" + price
				+ ", status=" + status + ", name=" + name + ", company="
				+ company + ", mobile=" + mobile + ", time=" + time
				+ ", termDate=" + termDate + ", fromDate=" + fromDate
				+ ", policyNo=" + policyNo + "]";
	}
	
}
