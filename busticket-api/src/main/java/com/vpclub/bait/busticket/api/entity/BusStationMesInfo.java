package com.vpclub.bait.busticket.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author  zengbaoyou
 * @E-mail:13678717317@139.com
 * @date 创建时间：2017年12月20日 上午17:25:40
 *
 */
@Data
@ApiModel("")
public class BusStationMesInfo {
	@ApiModelProperty("车站城市")
	private String origin;                          //车站城市
	@ApiModelProperty("发车客运站名称")
	private String stationName;                     //发车客运站名称
	@ApiModelProperty("发车客运站地址")
	private String stationAddress;                         //发车客运站地址
	@ApiModelProperty("发车客运站电话")
	private String stationPhone;                        //发车客运站电话
	@Override
	public String toString() {
		return "BusStationMesInfo [origin=" + origin + ", stationName="
				+ stationName + ", stationAddress=" + stationAddress
				+ ", stationPhone=" + stationPhone + "]";
	}
	
	
	
}
