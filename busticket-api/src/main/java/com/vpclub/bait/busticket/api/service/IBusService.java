package com.vpclub.bait.busticket.api.service;

import cn.vpclub.moses.core.model.response.BaseResponse;
import com.vpclub.bait.busticket.api.request.BusClassQueryRequest;
import com.vpclub.bait.busticket.api.request.BusTicketNewRequest;
import com.vpclub.bait.busticket.api.request.BusTicketRequest;
import com.vpclub.bait.busticket.api.request.InsuranceCallBackRequest;

public interface IBusService {

	public BaseResponse getOrigin(String userId);
	
	public BaseResponse returnBusOrder2(String userId);
	
	public BaseResponse bookBusTickets(String userId,BusTicketRequest busTicketRequest);



	//汽车票获取车站列表
	public BaseResponse getBusStation(String userId,String origin);
	
	//汽车票条件式筛选
	public BaseResponse getBusClass(String userId,BusClassQueryRequest busClassQueryRequest);
	
	//汽车票订单支付
	public BaseResponse busOrderPay(String userId,String orderCode);

	public void getBusOrderPay(String orderId);
	
	
	public Object doBusOrderPay(String userId,String orderCode) throws Exception;
	
	//汽车票取消订单
	public BaseResponse busOrderCancle(String userId,String orderCode,String type);
	
	public BaseResponse insuranceCallBack(InsuranceCallBackRequest insuranceCallBackRequest);
	
	/*
	 * 起始城市对应的终点城市列表
	 */
	public BaseResponse getSite(String userId,String origin) ;
	
	public BaseResponse returnBusOrder(String userId);
	

	//根据订单号查询汽车票订单详情
	public BaseResponse busDetailQueryByOrderId(String userId,String orderId);

	//汽车票下单
	public BaseResponse placeAnOrder(String userId, BusTicketNewRequest busTicketNewRequest);

}
