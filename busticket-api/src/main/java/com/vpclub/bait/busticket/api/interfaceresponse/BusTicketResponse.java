package com.vpclub.bait.busticket.api.interfaceresponse;

import lombok.Data;

/**
 * 第三方接口 汽车票下单响应参数
 * @author 曾保友
 * @E-mail zengbaoyou
 * @date 2015年12月11日 上午10:11:31
 */
@Data
public class BusTicketResponse {
	private String site;		 //目的站
	private String classCode; 	 //班次号
	private String status;		 //订单状态
	private String className; 	 //班次路线名称
	private String stationName; //发车客运站名称
	private String origin;		 //起点
	private String classTime;	 //班次发车时间
	private String bookTime;	 //下单时间
	private String amount;		 //订单金额
	private String price;		//票价
	private String name;		//旅客姓名
	private String cardNo;		//旅客证件号
	private String stationCode; //发车客运站编码
	private String orderCode;	 //订单编号
	private String tickets;  	//票数
	private String mobile;		//旅客电话
	private String seatNo;		 //锁定的座位号，多个座位已“,”号隔开
}
