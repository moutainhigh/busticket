//package com.vpclub.bait.busticket.command;
//
//import cn.vpclub.moses.core.enums.ReturnCodeEnum;
//import cn.vpclub.moses.core.model.response.BaseResponse;
//import cn.vpclub.moses.utils.common.IdWorker;
//import cn.vpclub.moses.utils.common.JsonUtil;
//import com.vpclub.bait.busticket.api.command.CreateBusOrderInfoCommand;
//import com.vpclub.bait.busticket.api.command.UpdateBusInsuranceInfoCommand;
//import com.vpclub.bait.busticket.api.config.InterfaceConfig;
//import com.vpclub.bait.busticket.api.dto.SubOrderDto;
//import com.vpclub.bait.busticket.api.entity.Customer;
//import com.vpclub.bait.busticket.api.interfaceresponse.BusTicketResponse;
//import com.vpclub.bait.busticket.api.request.BusTicketNewRequest;
//import com.vpclub.bait.busticket.api.request.BusTicketRequest;
//import com.vpclub.bait.busticket.api.request.PayRequest;
//import com.vpclub.bait.busticket.api.service.IPayNoticeService;
//import com.vpclub.bait.busticket.api.util.ValidateUtil;
//import com.vpclub.bait.busticket.command.config.PayConfig;
//import com.vpclub.bait.busticket.command.service.BusService;
//import com.vpclub.bait.busticket.command.service.BusServiceUtil;
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.methods.StringRequestEntity;
//import org.apache.commons.httpclient.params.HttpClientParams;
//import org.apache.commons.lang.time.DateUtils;
//import org.apache.http.HttpStatus;
//import org.axonframework.commandhandling.gateway.CommandGateway;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//public class BusticketCommandApplicationTests extends BaseTest{
//
//	@Autowired
//	CommandGateway commandGateway;
//	@Autowired
//	PayConfig payConfig;
//    @Autowired
//    InterfaceConfig interfaceConfig;
//    @Autowired
//    BusService busService;
//	@Autowired
//	IPayNoticeService payNoticeService;
//	@Autowired
//	BusServiceUtil busServiceUtil;
//	/**
//	 * 保险状态更新测试类
//	 */
////	@Test
//	public void contextLoads() {
//		try {
//			int status = 0;
//			String str = "投保成功";
//			UpdateBusInsuranceInfoCommand updateBusInsuranceInfoCommand = new UpdateBusInsuranceInfoCommand();
//			updateBusInsuranceInfoCommand.setOrderNo("1");
//			updateBusInsuranceInfoCommand.setCardNo("1");
//			updateBusInsuranceInfoCommand.setId(IdWorker.getId());
//			if("投保失败".equals(str)){
//				status= -1;
//			}else if("投保成功".equals(str)){
//				status= 2;
//				updateBusInsuranceInfoCommand.setPolicyNo("1");
//				updateBusInsuranceInfoCommand.setCompany("1");
//				String dateFormat[] = {"yyyy-MM-dd HH:mm:ss"};
//				updateBusInsuranceInfoCommand.setTermDate(DateUtils.parseDate("2017-12-26 12:00:00",dateFormat));
//				updateBusInsuranceInfoCommand.setFromDate(DateUtils.parseDate("2017-12-26 12:00:00",dateFormat));
//				updateBusInsuranceInfoCommand.setTime(DateUtils.parseDate("2017-12-26 12:00:00",dateFormat));
//			}
//			updateBusInsuranceInfoCommand.setStatus(status);
//			BaseResponse baseResponse = commandGateway.sendAndWait(updateBusInsuranceInfoCommand);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//	}
//
////	@Test
//	public void createOrder(){
//		BusTicketRequest request = new BusTicketRequest();
//		request.setCard("1");
//		request.setClassCode("1");
//		request.setDate("2017-12-10");
//		request.setMobile("13678717317");
//		request.setName("曾保友");
//		request.setOrderId("1");
//		request.setPrice("1.0");
//		request.setStationCode("NB");
//		request.setStationName("昆明");
//		request.setTicket("1");
//		request.setSite("普洱");
//		BusTicketResponse response = new BusTicketResponse();
//		response.setAmount("0.0");
//		response.setBookTime("2017-12-26 12:00:00");
//		response.setCardNo("1");
//		response.setClassCode("1");
//		response.setClassName("中班");
//		response.setOrigin("普洱");
//		response.setOrderCode("1");
//		response.setTickets("1");
//		response.setSeatNo("1");
//		response.setStatus("1");
//		response.setStationName("1");
//		response.setStationCode("1");
//		response.setClassTime("13:00");
//		CreateBusOrderInfoCommand createBusOrderInfoCommand = new CreateBusOrderInfoCommand();
//		createBusOrderInfoCommand.setBusTicketRequest(request);
//		createBusOrderInfoCommand.setBusTicketResponse(response);
//		createBusOrderInfoCommand.setUserId("1");
//		createBusOrderInfoCommand.setId(IdWorker.getId());
//		createBusOrderInfoCommand.setCreatedBy(1l);
//		createBusOrderInfoCommand.setCreatedTime(System.currentTimeMillis());
//		BaseResponse response1 = commandGateway.sendAndWait(createBusOrderInfoCommand);
//	}
//
////	@Test
//	public void payRequest(){
//
//		String orderNo = String.valueOf(System.currentTimeMillis());
//		String orderSubNo = String.valueOf(System.currentTimeMillis()-100);
//		PayRequest pr = new PayRequest();
//		pr.setOrderNo(orderNo);
//		pr.setOrderDesc("1111");
//		pr.setUserId("13678717317");
//		pr.setTotalAmount(100);
//		pr.setTelphone("13678717317");
//		pr.setBackNotifyUrl(payConfig.getBackNotifyUrl()+"?orderCode="+ orderNo);
//		pr.setFrontUrl(payConfig.getFrontUrl());
//		pr.setVersion(payConfig.getVersion());
//		pr.setAttach1("1");
//		pr.setAttach2("1");
//		pr.setAppId(payConfig.getAppID());
//		pr.setDevMode(String.valueOf(false));
//		List<SubOrderDto> subMaps = new ArrayList<>();
//		SubOrderDto dto = new SubOrderDto();
//		dto.setSubMerchOrderNo(orderSubNo);
//		dto.setLogisticsFee(0);
//		dto.setPayAmount(100);
//		dto.setPayStoreId(payConfig.getPayStoreId());
//		dto.setStoreId(payConfig.getStoreId());
//		subMaps.add(dto);
//		pr.setSubList(subMaps);
//
//		String payInfoJson = JsonUtil.objectToJson(pr);
//		Map<String, Object> paramMap = JsonUtil.jsonToMap(payInfoJson);
//		String sign  = ValidateUtil.getMD5SignFromMap(paramMap,"1a3b2d4k6f");
//		pr.setSign(sign);
//		String jsonStr = JsonUtil.objectToJson(pr);
//
////		String url = payConfig.getRequestUrl()+payConfig.getPayMethod();
//		String url = "http://rkdev.ynydhlw.com/bait/pay/command/createPay";
//		BaseResponse response= post(url,jsonStr);
//		if(response == null){
//			response = new BaseResponse();
//			response.setReturnCode(1005);
//			response.setMessage("发起支付请求失败！");
//		}
//		System.out.println(response);
//	}
//
//
//	private BaseResponse post(String url,String body){
//		BaseResponse ret = new BaseResponse();
//		String result = "";
//		try {
//			HttpClientParams hcp = new HttpClientParams();
//			hcp.setSoTimeout(10 * 1000);
//			hcp.setContentCharset("UTF-8");
//
//			HttpClient httpClient = new HttpClient(hcp);
//			PostMethod postMethod = new PostMethod(url);
//			postMethod.addRequestHeader("Content-Type","application/json; charset=UTF-8");
//			StringRequestEntity requestEntity = new StringRequestEntity(body);
//			postMethod.setRequestEntity(requestEntity);
//			int statusCode = httpClient.executeMethod(postMethod);
//			if (statusCode == HttpStatus.SC_OK) {
//				result = postMethod.getResponseBodyAsString();
//			} else {
//
//			}
//			if(org.apache.commons.lang.StringUtils.isNotBlank(result)){
//				JSONObject retJson = JSONObject.fromObject(result);
//				ret = (BaseResponse)JSONObject.toBean(retJson,BaseResponse.class);
//			}else {
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//			ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
//			ret.setMessage(e.getMessage());
//		}
//		return ret;
//	}
//
////	@Test
//    public void placeOrder(){
//
//		BusTicketNewRequest bt = new BusTicketNewRequest();
//		bt.setInsBackUrl(interfaceConfig.getBackUrl());
//		bt.setCard("532621198606191339");
//		bt.setName("李栋");
//		bt.setMobile("18827417161");
//		bt.setPrice("0.5");
//		bt.setStationCode("XB");
//		bt.setStationName("西部客运站");
//		bt.setTicket("1");
//		bt.setClassCode("10003");
//		Customer customer = new Customer();
//		customer.setCardNo("532621198606191339");
//		customer.setMobile("18827417161");
//		customer.setName("李栋");
//		customer.setInsurancePrice("1");
//		List<Customer> list = new ArrayList<>();
//		list.add(customer);
//		bt.setCustomers(list);
//		bt.setDate("2018-02-03");
//		bt.setSite("大理");
//		BaseResponse response = busService.placeAnOrder("13678717317",bt);
//		System.out.println(response);
//	}
//
//	/**
//	 * 支付回调单元测试
//	 */
////	@Test
//	public void payCallback(){
//		Map m = new HashMap();
//		m.put("appId", "10000168");
//		m.put("totalAmount", 10);
//		m.put("payType", "微信支付");
//		m.put("payPlatform", "YNQJS");
//		m.put("orderNo", "sdfsdfadf111");
//		m.put("transactionNo", "111111");
//		m.put("tradeStatus", "支付成功");
//		m.put("attach1", null);
//		m.put("attach2", null);
//		String sign = ValidateUtil.getSignFromMap(m, "1a3b2d4k6f", "MD5");
//		m.put("sign", sign);
//
//		BaseResponse response = payNoticeService.payNotice("",JSONObject.fromObject(m).toString());
//		System.out.println(response);
//	}
//
////	@Test
//	public void sendSms(){
//		busServiceUtil.sendMessage("13678717317","短信发送测试");
//	}
//}
