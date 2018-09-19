package com.vpclub.bait.busticket.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author  zengbaoyou
 * @E-mail:13678717317@139.com
 * @date 创建时间：2017年12月20日 上午17:25:56
 *
 */
@Data
@ApiModel("")
public class BusDoPayInfo {
	@ApiModelProperty("主键ID")
	private Integer id;
	@ApiModelProperty("汽车票订单编号")
	private String orderCode;                        //汽车票订单编号
	@ApiModelProperty("用户id")
	private String userId;                           //用户id
	@ApiModelProperty("汽车旅客名称")
	private String name;                             //汽车旅客名称
	@ApiModelProperty("乘客联系电话")
	private String mobile;                           //乘客联系电话
	@ApiModelProperty("座位号")
	private String seatno;                           //座位号
	@ApiModelProperty("班次发车时间")
	private String classtime;                        //班次发车时间
	@ApiModelProperty("出发城市")
	private String origin;                           //出发城市
	@ApiModelProperty("目的地城市")
	private String site;                             //目的地城市
	@ApiModelProperty("出发客运站站点")
	private String stationName;                      //出发客运站站点
	@ApiModelProperty("客运班次")
	private String classcode;                        //客运班次
	@ApiModelProperty("身份证号码")
	private String card;                             //身份证号码
	
	@Override
	public String toString() {
		return "BusDoPayInfo [id=" + id + ", orderCode=" + orderCode
				+ ", userId=" + userId + ", name=" + name + ", mobile="
				+ mobile + ", seatno=" + seatno + ", classtime=" + classtime
				+ ", origin=" + origin + ", site=" + site + ", stationName="
				+ stationName + ", classcode=" + classcode + ", card=" + card
				+ "]";
	}
	
	
}
