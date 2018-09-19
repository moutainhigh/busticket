package com.vpclub.bait.busticket.api.entity;

import lombok.Data;

@Data
public class StationInfo {
	private String origin;                 //所属地区名称
	private String stationCode;            //客运站编码
	private String stationName;            //客运站
	private String address;                //客运站地址
}
