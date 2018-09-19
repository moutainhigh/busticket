package com.vpclub.bait.busticket.api.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 *
 * @author  zengbaoyou
 * @E-mail:13678717317@139.com
 * @date 创建时间：2017年12月20日 上午17:25:40
 */
@Data
public class InsuranceCallBackInfo {

    private String cardNo;
    private String time;
    private int price;
    private String termDate;
    private String fromDate;
    private String status;
    private String name;
    private String company;
    private String policyNo;
    private String mobile;
    
	@Override
	public String toString() {
		return "InsuranceCallBackInfo [cardNo=" + cardNo + ", time=" + time
				+ ", price=" + price + ", termDate=" + termDate + ", fromDate="
				+ fromDate + ", status=" + status + ", name=" + name
				+ ", company=" + company + ", policyNo=" + policyNo
				+ ", mobile=" + mobile + "]";
	}
	
    
}
