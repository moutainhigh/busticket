package com.vpclub.bait.busticket.api.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 汽车票订单
 * @author 曾保友
 * @E-mail zeng.baoyou@vpclub.com
 * @date 2017年12月25日 上午10:39:05
 */
@Data
public class BusTicketOrder {
	private String site;		 //目的站点名称
	private String classCode;	 //班次名称
	private String status;
	private String className;
	private String stationName;
	private String origin;
	private String classTime;
	private String bookTime; 	  //下单时间
	private String password;	  //取票密码，只有订单状态为出票时才有效。
	private String amount;		  //订单金额
	private List<InsuranceInfo> insuranceInfo; //保险信息
	private String price;
	private String name;
	private String cardNo;
	private String stationCode;  //发车客运站编码
	private String orderCode;
	private String tickets; 	  //票数
	private String mobile;		  //联系人电话
	private String seatNo;
	private String insPrice;            //保险价格
	private String insNum;              //保险数量
	private String stationAddress;             //发车客运站地址
	private List<String> stationPhones = new ArrayList<String>();            //发车客运站电话
	private String orderId;                    //商品订单编号

}
