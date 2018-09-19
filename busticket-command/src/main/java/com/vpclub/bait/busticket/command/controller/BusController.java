package com.vpclub.bait.busticket.command.controller;

import cn.vpclub.moses.core.enums.ReturnCodeEnum;
import cn.vpclub.moses.core.model.response.BaseResponse;
import com.vpclub.bait.busticket.api.entity.*;
import com.vpclub.bait.busticket.api.request.BusClassQueryRequest;
import com.vpclub.bait.busticket.api.request.BusTicketNewRequest;
import com.vpclub.bait.busticket.api.request.BusTicketRequest;
import com.vpclub.bait.busticket.api.request.InsuranceCallBackRequest;
import com.vpclub.bait.busticket.api.service.IPayNoticeService;
import com.vpclub.bait.busticket.api.service.IPaymentService;
import com.vpclub.bait.busticket.api.service.IRefundNoticeService;
import com.vpclub.bait.busticket.command.service.BusService;
import com.vpclub.bait.busticket.command.service.PaymentService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 汽车票接口
 * @author 曾保友
 * @E-mail zeng.baoyou@vpclub.cn
 * @date 2017年12月26日 下午09:00:00
 * */

@RestController
//@RequestMapping("/erkuai")
public class BusController {
	private Logger log= LoggerFactory.getLogger(getClass());
	
	@Autowired
	private BusService busService;
	@Autowired
	private IPayNoticeService payNoticeService;
	@Autowired
	private IRefundNoticeService refundNoticeService;
	@Autowired
	private IPaymentService paymentService;

   //查询起始城市
	@RequestMapping(value="/{userId}/bus/origin",method= RequestMethod.GET)
	public BaseResponse getBusOrigin(@PathVariable String userId, HttpServletRequest request){
		log.info("BusController returnOrigin userId="+userId);
		BaseResponse ret=busService.getOrigin(userId);
		log.info("BusController returnOrigin ret="+ret);
		return ret;
	}

	//根据起始城市查询车站
	@RequestMapping(value = "/{userId}/bus/station",method = RequestMethod.POST)
	public BaseResponse getBusStation(@PathVariable String userId, @RequestBody BusSiteQueryRequest busInitial, HttpServletRequest request) {
		log.info("-->- BusController getBusStation"+ " userId: " + userId + " ,origin" + busInitial.getOrigin());
		BaseResponse ret = busService.getBusStation(userId, busInitial.getOrigin());
		log.info("--<- BusController getBusStation response: " + ret);
		return ret;
	}
	

	//查询汽车班次
	@RequestMapping(value = "/{userId}/bus/class",method = RequestMethod.POST)
	public BaseResponse getBusClass(@PathVariable String userId, @RequestBody BusClassQueryRequest busClass,
                                  HttpServletRequest request) {
		log.info("-->- BusController getBusClass"+ " userId: " + userId + " ,BusClassQueryReq" + busClass);
		BaseResponse ret = busService.getBusClass(userId, busClass);
//		log.info("--<- BusController getBusClass response: " + ret);
		return ret;
	}
    //查询汽车票订单的接口
	@RequestMapping(value="/{userId}/bus/order/query",method= RequestMethod.GET)
	public BaseResponse getBusOrder(@PathVariable String userId, HttpServletRequest request){
		log.info("BusController queryOrder params userId="+userId);
		BaseResponse ret=busService.returnBusOrder(userId);
		log.info("BusController queryOrder ret="+ret);
		return ret;
	}
	//下单的接口
	@RequestMapping(value="/{userId}/bus/order",method= RequestMethod.PUT)
	public BaseResponse bookBusTicket(@PathVariable String userId, @RequestBody BusTicketNewRequest busTicketReq, HttpServletRequest request){
		log.info("BusController bookBusTicket params userId:"+userId+",BusTicketReq busTicketReq:"+busTicketReq);
		BaseResponse ret=busService.placeAnOrder(userId,busTicketReq);
		log.info("BusController bookBusTicket ret="+ret);
		return ret;	
	}
	
//	@RequestMapping(value="/{userId}/bus/order/pay",method=RequestMethod.POST)
//	public ReturnCode busOrderPay(@PathVariable String userId,@RequestBody BusOrderCancleReq orderCancle, 
//			HttpServletRequest request){
//		log.info("BusController orderPay params userId="+userId+",orderCode="+orderCancle.getOrderCode());
//		ReturnCode ret=busService.busOrderPay(userId,orderCancle.getOrderCode());
//		log.info("BusController orderPay ret="+ret);
//		return ret;
//	}
	
//	@RequestMapping(value="/{userId}/bus/order",method=RequestMethod.DELETE)
//	public ReturnCode orderCancle(@PathVariable String userId,@RequestBody BusOrderCancleReq orderCancle, 
//			HttpServletRequest request){
//		log.info("BusController orderCancle params userId="+userId+",orderCancle="+orderCancle);
//		ReturnCode ret=busService.busOrderCancle(userId,orderCancle);
//		log.info("BusController orderCancle ret="+ret);
//		return ret;
//	}

	//订单取消
	@RequestMapping(value="/{userId}/bus/order/{ordercode}",method= RequestMethod.DELETE)
	public BaseResponse orderCancle(@PathVariable String userId, @PathVariable String ordercode){
		log.info("BusController orderCancle params userId =>" + userId + ",ordercode =>" + ordercode);
		BaseResponse ret=busService.busOrderCancle(userId,ordercode,"0");
		log.info("BusController orderCancle ret =>" + ret);
		return ret;
	}
	//保险
	@RequestMapping(value="/bus/insurance/callback",method= RequestMethod.POST)
	public BaseResponse insuranceCallBack(@RequestBody InsuranceCallBackRequest callBackBean, HttpServletRequest request){
		log.info("BusController insuranceCallBack InsuranceCallBackBean=>" + callBackBean);
		BaseResponse ret = busService.insuranceCallBack(callBackBean);
		log.info("BusController insuranceCallBack ret="+ret);
		return ret;
	}
//支付回调
	@RequestMapping(value="/bus/pay/notice",method= RequestMethod.POST)
	public BaseResponse payNotice(@RequestParam("orderCode") String orderCode, HttpServletRequest request,@RequestBody String json){
		log.info("BusController orderCode=>{},json=>{}" ,orderCode,json);
		BaseResponse ret = payNoticeService.payNotice(orderCode,json);
		log.info("BusController insuranceCallBack ret="+ret);
		return ret;
	}
//退款回调
	@RequestMapping(value="/bus/refund/notice",method= RequestMethod.POST)
	public BaseResponse refundNotice(@RequestParam("orderCode") String orderCode, HttpServletRequest request,@RequestBody String json){
		log.info("BusController orderCode=>" + orderCode);
		BaseResponse ret = refundNoticeService.refundNotice(orderCode,json);
		log.info("BusController insuranceCallBack ret="+ret);
		return ret;
	}

	//查询可达城市
	@RequestMapping(value="/{userId}/bus/siteQuery",method= RequestMethod.POST)
	public BaseResponse getSite(@PathVariable String userId, @RequestBody BusSiteQueryRequest busInitial, HttpServletRequest request ){
		log.info("BusController getSite userId=>" + userId +",BusInitial busInitial:" + busInitial);
		BaseResponse ret = busService.getSite(userId,busInitial.getOrigin());
//		log.info("BusController getSite ret=" +ret);
		return ret;
		
	}

	//获取汽车票订单详情
	@RequestMapping(value="/{userId}/bus/order/detail/{orderCode}",method= RequestMethod.GET)
	public BaseResponse getBusDetailByOrderId(@PathVariable String userId, @PathVariable String orderCode, HttpServletRequest request ){
		log.info("getBusDetailByOrderId userId=>" + userId +",orderCode:" + orderCode);
		BaseResponse ret = busService.busDetailQueryByOrderId(userId,orderCode);
		log.info("getBusDetailByOrderId ret=" +ret);
		return ret;
	}

    //下单支付(发起支付请求)
	@RequestMapping(value="/{userId}/bus/order/payRequest/{orderCode}",method= RequestMethod.POST)
	public BaseResponse payRequest(@PathVariable String userId, @PathVariable String orderCode, HttpServletRequest request ,@RequestBody JSONObject obj ){
		BaseResponse ret = null;
				log.info("payRequest userId=>" + userId +",orderCode:" + orderCode);
		if(obj.has("frontUrl")&& StringUtils.isNotBlank(obj.getString("frontUrl"))){
			ret = paymentService.payRequest(userId,orderCode,obj.getString("frontUrl"));
		}else {
			ret.setReturnCode(ReturnCodeEnum.CODE_1006.getCode());
			ret.setMessage("frontUrl不能为空");
		}

		log.info("payRequest ret=" +ret);
		return ret;
	}

	public static void main(String[] args) {
		List<String> haha = new ArrayList<>();
		haha.add("1");
		haha.add("2");
		JSONArray array = JSONArray.fromObject(haha);
		System.out.println(array.toString());
		System.out.println(JSONObject.fromObject(new InsuranceInfo()).toString());
	}
}
