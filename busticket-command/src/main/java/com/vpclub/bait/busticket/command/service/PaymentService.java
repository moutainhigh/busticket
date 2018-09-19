package com.vpclub.bait.busticket.command.service;

import cn.vpclub.moses.core.enums.ReturnCodeEnum;
import cn.vpclub.moses.core.model.response.BaseResponse;
import cn.vpclub.moses.utils.common.JsonUtil;
import com.vpclub.bait.busticket.api.config.InterfaceConfig;
import com.vpclub.bait.busticket.api.dto.SubOrderDto;
import com.vpclub.bait.busticket.api.entity.BusOrderPayInfo;
import com.vpclub.bait.busticket.api.entity.BusTicketOrder;
import com.vpclub.bait.busticket.api.entity.OrderInfo;
import com.vpclub.bait.busticket.api.entity.PayRequestResult;
import com.vpclub.bait.busticket.api.enums.OrderStatus;
import com.vpclub.bait.busticket.api.request.PayRequest;
import com.vpclub.bait.busticket.api.service.IPayNoticeService;
import com.vpclub.bait.busticket.api.service.IPaymentService;
import com.vpclub.bait.busticket.api.service.IRefundNoticeService;
import com.vpclub.bait.busticket.api.service.IRefundService;
import com.vpclub.bait.busticket.api.util.ValidateUtil;
import com.vpclub.bait.busticket.command.config.PayConfig;
import com.vpclub.bait.busticket.command.rpc.GrpcService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class PaymentService implements IPaymentService,IPayNoticeService,IRefundNoticeService,IRefundService{

    @Autowired
    GrpcService grpcService;
    @Autowired
    BusServiceUtil busServiceUtil;
    @Autowired
    PayConfig payConfig;

    @Autowired
    InterfaceConfig interfaceConfig;
    @Override
    //下单支付
    public BaseResponse payRequest(String userId, String orderCode,String frontUrl) {
        BaseResponse ret = new BaseResponse();
        List<OrderInfo> list = grpcService.queryByOrderIdAndUserId(orderCode,userId);
        BusTicketOrder busTicketOrder = null;
        try {
            busTicketOrder = busServiceUtil.doReturnBusOrder(userId, busServiceUtil.getTokenString(),orderCode);
        } catch (Exception e) {
            ret.setReturnCode(1005);
            ret.setMessage("发起支付请求时未查询到订单");
            return ret;
        }
        if((list == null || list.size()==0) && busTicketOrder==null){
            ret.setReturnCode(1005);
            ret.setMessage("发起支付请求时未查询到订单");
            return ret;
        }
        OrderInfo orderInfo = list.get(0);
        try {
            String orderSubNo = String.valueOf(System.currentTimeMillis()-100);
            PayRequest pr = new PayRequest();
            pr.setOrderNo(orderCode);
            pr.setOrderDesc("buyBusticket");
            pr.setUserId(userId);
            pr.setTotalAmount(new BigDecimal(orderInfo.getAmount()).add(new BigDecimal(orderInfo.getServiceAmount())).multiply(new BigDecimal("100")).intValue());
            pr.setTelphone(orderInfo.getMobile());
            pr.setBackNotifyUrl(payConfig.getBackNotifyUrl()+"?orderCode="+ orderCode);
            pr.setFrontUrl(frontUrl);
            pr.setVersion(payConfig.getVersion());
            pr.setAttach1(null);
            pr.setAttach2(null);
            pr.setAppId(payConfig.getAppID());
            pr.setDevMode(String.valueOf(payConfig.getDevMode()));
            List<SubOrderDto> subMaps = new ArrayList<>();
            SubOrderDto dto = new SubOrderDto();
            dto.setSubMerchOrderNo(orderInfo.getId()+"");
            dto.setLogisticsFee(0);
            dto.setPayAmount(new BigDecimal(orderInfo.getAmount()).add(new BigDecimal(orderInfo.getServiceAmount())).multiply(new BigDecimal("100")).intValue());
            dto.setPayStoreId(payConfig.getPayStoreId());
            dto.setStoreId(payConfig.getStoreId());
            subMaps.add(dto);
            pr.setSubList(subMaps);

            String payInfoJson = JsonUtil.objectToJson(pr);
            log.info("发送的支付信息：{}",payInfoJson);
            Map<String, Object> paramMap = JsonUtil.jsonToMap(payInfoJson);
            String sign  = ValidateUtil.getMD5SignFromMap(paramMap,"1a3b2d4k6f");
            pr.setSign(sign);
            String jsonStr = JsonUtil.objectToJson(pr);

//            String url = payConfig.getRequestUrl()+payConfig.getPayMethod();
            String url = payConfig.getPayMethod();

            log.info("payRequest#url={},payRequest->data={}",url,jsonStr);
            BaseResponse response= post(url,jsonStr);
            if(response == null){
                response = new BaseResponse();
                response.setReturnCode(1005);
                response.setMessage("发起支付失败！");
            }
            ret = response;
        }catch (Exception e){
            log.error("订单号为：{}的车票订单，发起支付请求异常：{}",orderCode,e.getMessage());
        }
        return ret;
    }

    //支付回调
    @Override
    public BaseResponse payNotice(String orderCode,String json) {
        BaseResponse ret = new BaseResponse();
        //验证签名
        if(!validateSign(json)){
            ret.setReturnCode(ReturnCodeEnum.CODE_1010.getCode());
            ret.setMessage("签名验证失败");
            return ret;
        }
        try{
            List<OrderInfo> list = grpcService.queryByOrderIdAndUserId(orderCode,null);
            if((list == null || list.size()==0)){
                ret.setReturnCode(1005);
                ret.setMessage("支付异步通知失败，未查询到订单号为"+orderCode+"的订单");
                log.error("支付异步通知失败，未查询到订单号为:{}的订单",orderCode);
                return ret;
            }
            //支付成功修改订单的状态
            Boolean flag = grpcService.updateOrderStatus(list.get(0).getId(), OrderStatus.SUCCESS_TRADE.getValue());
            //通知第三方已经车票支付成功
            /*
            * 1、因为实际已经支付成功了，所以先记录本地订单支付状态；
            * 2、通知供票方，已支付成功；
            * */
            String tokenString = busServiceUtil.getTokenString();
            log.info("PaymentService payNotice getTokenString tokenString:"+tokenString);
            String resultJson = busServiceUtil.orderPay(tokenString,orderCode);
            log.info("PaymentService payNotice getResultJson resultJson:"+resultJson);
            JSONObject jsonObject = JSONObject.fromObject(resultJson);

            log.info("payNotice#jsonObject="+jsonObject.toString());
            String reason = busServiceUtil.getErrorInfo(jsonObject,"busOrderPay");
            if(StringUtils.isNotBlank(reason)){
                String message = busServiceUtil.getBusMessage(list.get(0), null);
                busServiceUtil.sendMessage(list.get(0).getMobile(), message);
            }else{
                BusOrderPayInfo orderpayInfo =(BusOrderPayInfo) JSONObject.toBean(jsonObject, BusOrderPayInfo.class);
                log.info("payNotice"+orderpayInfo.toString());
            }
            ret.setReturnCode(flag?ReturnCodeEnum.CODE_1000.getCode():ReturnCodeEnum.CODE_1005.getCode());
            if (!flag){
                ret.setMessage("支付回调处理失败");
            }
        }catch (Exception e){
            log.error("PaymentService#payNotice#{}",e.getMessage());
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage("系统发生异常"+e.getCause());
        }
        return ret;
    }

    @Override
    public BaseResponse refundNotice(String orderCode,String json) {
        BaseResponse ret = new BaseResponse();
        //验证签名
        if(!validateSign(json)){
            ret.setReturnCode(ReturnCodeEnum.CODE_1010.getCode());
            ret.setMessage("签名验证失败");
            return ret;
        }
        try{
            List<OrderInfo> list = grpcService.queryByOrderIdAndUserId(orderCode,null);
            if((list == null || list.size()==0)){
                ret.setReturnCode(1005);
                ret.setMessage("退款异步通知失败，未查询到订单号为"+orderCode+"的订单");
                log.error("退款异步通知失败，未查询到订单号为:{}的订单",orderCode);
                return ret;
            }
            Boolean flag = grpcService.updateOrderStatus(list.get(0).getId(),OrderStatus.REFUNDED.getValue());
            ret.setReturnCode(flag?ReturnCodeEnum.CODE_1000.getCode():ReturnCodeEnum.CODE_1005.getCode());
            if (!flag){
                ret.setMessage("退款回调处理失败");
            }
        }catch (Exception e){
            log.error("PaymentService#refundNotice#{}",e.getMessage());
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage("系统发生异常"+e.getCause());
        }
        return ret;
    }

    @Override
    public BaseResponse refund(String userId, String orderCode) {
        BaseResponse ret = new BaseResponse();
        JSONObject json = new JSONObject();
        List<OrderInfo> list = grpcService.queryByOrderIdAndUserId(orderCode,userId);
        OrderInfo orderInfo = list.get(0);
        json.put("subOrderNo",orderInfo.getId());
        json.put("storeId",payConfig.getStoreId());
        json.put("notifyUrl",payConfig.getRefundNoticeUrl()+"?orderCode="+ orderCode);
        json.put("refundAmount",new BigDecimal(orderInfo.getAmount()).multiply(new BigDecimal("100")).longValue());
        json.put("telphone",orderInfo.getMobile());
        json.put("payStoreId",payConfig.getPayStoreId());
        json.put("appId",payConfig.getAppID());
//        String url = payConfig.getRequestUrl()+payConfig.getRefundMethod();
        String url = payConfig.getRefundMethod();
        BaseResponse response= post(url,json.toString());
        if(response == null){
            response = new BaseResponse();
            response.setReturnCode(1005);
            response.setMessage("发起退款失败！");
        }
        log.info("用户id：{}的用户，发起订单编号为orderCode：{}订单退款结果：{}",userId,orderCode,response.toString());
        ret = response;
        return ret;
    }


    private BaseResponse post(String url,String body){
        BaseResponse ret = new BaseResponse();
        String result = "";
        try {
            HttpClientParams hcp = new HttpClientParams();
            hcp.setSoTimeout(20 * 1000);
            hcp.setContentCharset("UTF-8");

            HttpClient httpClient = new HttpClient(hcp);
            PostMethod postMethod = new PostMethod(url);
            postMethod.addRequestHeader("Content-Type","application/json; charset=UTF-8");
            StringRequestEntity requestEntity = new StringRequestEntity(body);
            postMethod.setRequestEntity(requestEntity);
            int statusCode = httpClient.executeMethod(postMethod);
            log.info("post#url={},data={},result={}",url,body,postMethod.getResponseBodyAsString());
            if (statusCode == HttpStatus.SC_OK) {
                result = postMethod.getResponseBodyAsString();
            } else {

            }
            if(org.apache.commons.lang.StringUtils.isNotBlank(result)){
                JSONObject retJson = JSONObject.fromObject(result);
                ret.setReturnCode(retJson.getInt("returnCode"));
                ret.setMessage(retJson.getString("message"));
                if(retJson.getJSONObject("dataInfo").has("payUrl")){
                    PayRequestResult requestResult = new PayRequestResult();
                    requestResult.setPayUrl(retJson.getJSONObject("dataInfo").getString("payUrl"));
                    requestResult.setSessionId(retJson.getJSONObject("dataInfo").getString("sessionId"));
                    ret.setDataInfo(requestResult);
                }
            }else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 签名验证
     * @param json
     * @return
     */
    private boolean validateSign(String json){
        log.info("签名信息:{}",json);
        JSONObject jsonObject = JSONObject.fromObject(json);
        if(json == null||!jsonObject.has("sign")){
            return  false;
        }
        Set<String> keys = jsonObject.keySet();
        Map m = new HashMap();
        for (String key:keys) {
            if(!key.equals("sign")){
                m.put(key, jsonObject.get(key).equals(JSONNull.getInstance())?null:jsonObject.get(key));
            }
        }
        return ValidateUtil.getSignFromMap(m,"1a3b2d4k6f", "MD5").equals(jsonObject.getString("sign"));
    }
}
