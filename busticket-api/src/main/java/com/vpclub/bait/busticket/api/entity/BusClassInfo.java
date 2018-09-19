package com.vpclub.bait.busticket.api.entity;

import lombok.Data;

@Data
public class BusClassInfo {
	private String className;                 //班次线路名称
	private String classCode;                 //班次编码
	private String status;                    //班次状态（正常：该班次可售）
	private String carLevel;                  //车辆等级
	private String carType;                   //车辆类型
	private String origin;                    //起点
	private String site;                      //目的站点
	private String stationCode;               //发车客运站编码
	private String stationName;               //发车客运站名称
	private String stationLeg;                //里程（单位：公里）
	private String price;                     //票价
	private String seats;                     //总的座位数
	private String resticket;                 //剩余座位数
	private String useDate;                   //发班日期
	private String useTime;                   //发班时间
	private String expectArrivalTime;//预计到达时间

}






