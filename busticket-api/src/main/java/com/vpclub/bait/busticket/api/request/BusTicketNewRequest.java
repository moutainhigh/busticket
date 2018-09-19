package com.vpclub.bait.busticket.api.request;

import cn.vpclub.moses.utils.validator.annotations.NotEmpty;
import com.vpclub.bait.busticket.api.entity.Customer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author  zengbaoyou
 * @E-mail:13678717317@139.com
 * @date 创建时间：2017年12月21日 上午17:28:56
 *
 */
@Data
@ApiModel("订票参数封装")
@NoArgsConstructor
@AllArgsConstructor
public class BusTicketNewRequest {
	@ApiModelProperty("发车客运站编码")
	@NotEmpty
	private String stationCode; 	//发车客运站编码（必须）
	@ApiModelProperty("班次号")
	@NotEmpty
	private String classCode; 	 //班次号（必须）
	@ApiModelProperty("目的站")
	@NotEmpty
	private String site;		 //目的站（必须）
	@ApiModelProperty("出发日期")
	@NotEmpty
	private String date;		 //出发日期（必须）
	@ApiModelProperty("票价")
	@NotEmpty
	private String price;		//票价（必须）
	@ApiModelProperty("票数")
	@NotEmpty
	private String ticket;  	//票数（必须）
	@ApiModelProperty("旅客姓名")
	@NotEmpty
	private String name;		//旅客姓名（必须）
	@ApiModelProperty("旅客电话")
	@NotEmpty
	private String mobile;		//旅客电话（必须）
	@ApiModelProperty("证件号")
	private String card;			//证件号
	@ApiModelProperty("保险地址")
	private String insBackUrl;		//保险地址
	@ApiModelProperty("乘客信息")
	private List<Customer> customers;			//乘客信息
	@ApiModelProperty("发车客运站名称")
	private String stationName;                //发车客运站名称
	
	
	@Override
	public String toString() {
		return "BusTicketNewReq [stationCode=" + stationCode + ", classCode="
				+ classCode + ", site=" + site + ", date=" + date + ", price="
				+ price + ", ticket=" + ticket + ", name=" + name + ", mobile="
				+ mobile + ", card=" + card + ", insBackUrl=" + insBackUrl
				+ ", customers=" + customers + ", stationName=" + stationName
				+ "]";
	}
	
}
