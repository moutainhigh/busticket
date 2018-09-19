package com.vpclub.bait.busticket.query.handlers;

import cn.vpclub.moses.core.model.response.BaseResponse;
import cn.vpclub.moses.utils.common.IdWorker;
import com.vpclub.bait.busticket.api.entity.Customer;
import com.vpclub.bait.busticket.api.entity.InsuranceCustomer;
import com.vpclub.bait.busticket.api.event.BusCustomerInfoBatchCreateEvent;
import com.vpclub.bait.busticket.api.event.BusInsuranceInfoCreateEvent;
import com.vpclub.bait.busticket.api.event.BusInsuranceInfoUpdateEvent;
import com.vpclub.bait.busticket.api.event.BusOrderInfoCreateEvent;
import com.vpclub.bait.busticket.api.util.OrderUtil;
import com.vpclub.bait.busticket.query.entity.BusCustomerInfo;
import com.vpclub.bait.busticket.query.entity.BusInsuranceInfo;
import com.vpclub.bait.busticket.query.entity.BusOrderInfo;
import com.vpclub.bait.busticket.query.service.impl.BusCustomerInfoService;
import com.vpclub.bait.busticket.query.service.impl.BusInsuranceInfoService;
import com.vpclub.bait.busticket.query.service.impl.BusOrderInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
@ProcessingGroup("message")
/**
 * @auth 曾保友
 * 客户信息事件处理类
 */
public class BusTicketEventHandler {
    @Autowired
    BusCustomerInfoService busCustomerInfoService;
    @Autowired
    BusOrderInfoService busOrderInfoService;
    @Autowired
    BusInsuranceInfoService busInsuranceInfoService;
    /**
     * 批量新增乘客
     * @param event
     * @return
     */
    @EventHandler
    public BaseResponse handler(BusCustomerInfoBatchCreateEvent event){
        List<BusCustomerInfo> busCustomerInfos = new ArrayList<>();
        BaseResponse baseResponse = new BaseResponse();
        for (Customer customer:
                event.getCustomers()) {
            BusCustomerInfo busCustomerInfo = new BusCustomerInfo();
            BeanUtils.copyProperties(customer,busCustomerInfo);
            busCustomerInfo.setOrderCode(event.getOrderCode());
            busCustomerInfo.setCreatedBy(event.getCreatedBy());
            Long times = System.currentTimeMillis();
            busCustomerInfo.setCreatedBy(times);
            busCustomerInfo.setCreatedTime(times);
            busCustomerInfos.add(busCustomerInfo);
        }
        if(busCustomerInfos.size()>0){
            try {
                boolean flag = busCustomerInfoService.insertBatch(busCustomerInfos);
                if(flag){
                    baseResponse.setReturnCode(1000);
                }else {
                    baseResponse.setReturnCode(1005);
                    baseResponse.setMessage("乘客信息新增错误");
                }
            } catch (Exception e) {
                baseResponse.setReturnCode(1005);
                baseResponse.setMessage("乘客信息新增错误："+e.getMessage());
            }
        }else {
            baseResponse.setReturnCode(1005);
            baseResponse.setMessage("乘客信息为空，不执行新增操作。");
        }
        return baseResponse;
    }

    /**
     * 下单处理
     * @param event
     * @return
     */
    @EventHandler
    public BaseResponse handler(BusOrderInfoCreateEvent event){
        BaseResponse baseResponse = new BaseResponse();
        try {
            BusOrderInfo busOrderInfo = new BusOrderInfo();
            busOrderInfo.setId(event.getId());
            busOrderInfo.setClassCode(event.getBusTicketRequest().getClassCode());
            busOrderInfo.setSite(event.getBusTicketRequest().getSite());
            busOrderInfo.setStationCode(event.getBusTicketRequest().getStationCode());
            double price=Double.parseDouble(event.getBusTicketRequest().getPrice());
            busOrderInfo.setPrice(price);
            String format[] = {"yyyy-mm-dd"};
            busOrderInfo.setClassDate(DateUtils.parseDate(event.getBusTicketRequest().getDate(),format));
            int ticket=Integer.parseInt(event.getBusTicketRequest().getTicket());
            busOrderInfo.setTicket(ticket);
            busOrderInfo.setName(event.getBusTicketRequest().getName());
            busOrderInfo.setMobile(event.getBusTicketRequest().getMobile());
            busOrderInfo.setCard(event.getBusTicketRequest().getCard());
            busOrderInfo.setStatus(OrderUtil.getBusOrderStatus(event.getBusTicketResponse().getStatus()));
            busOrderInfo.setOrigin(event.getBusTicketResponse().getOrigin());
            busOrderInfo.setClassTime(event.getBusTicketResponse().getClassTime());
            busOrderInfo.setBookTime(event.getBusTicketResponse().getBookTime());
            double amount=Double.parseDouble(event.getBusTicketResponse().getAmount());
            busOrderInfo.setAmount(amount);
            busOrderInfo.setOrderCode(event.getBusTicketResponse().getOrderCode());
            busOrderInfo.setSeatNo(event.getBusTicketResponse().getSeatNo());
            busOrderInfo.setUserId(event.getUserId());
            Long times = System.currentTimeMillis();
            busOrderInfo.setCreatedBy(times);
            busOrderInfo.setCreatedTime(times);
            busOrderInfo.setInsurAmount(event.getInsurAmount());
            busOrderInfo.setServiceAmount(event.getServiceAmount());
            boolean success = busOrderInfoService.insert(busOrderInfo);
            if(success){
                baseResponse.setReturnCode(1000);
            }else {
                baseResponse.setReturnCode(1005);
                baseResponse.setMessage("下单失败");
            }
        } catch (Exception e) {
            baseResponse.setReturnCode(1005);
            baseResponse.setMessage("下单失败："+e.getMessage());
        }
        return baseResponse;
    }

    /**
     * 保险批量插入
     * @param event
     * @return
     */
    @EventHandler
    public BaseResponse handler(BusInsuranceInfoCreateEvent event){
        BaseResponse baseResponse = new BaseResponse();
        List<BusInsuranceInfo> busInsuranceInfos = new ArrayList<>();
        try {
            for (InsuranceCustomer customer:event.getInsuranceCustomers()
                 ) {
                BusInsuranceInfo busInsuranceInfo = new BusInsuranceInfo();
                busInsuranceInfo.setId(IdWorker.getId());
                BeanUtils.copyProperties(customer,busInsuranceInfo);
                busInsuranceInfo.setPrice(event.getPrice());
                busInsuranceInfo.setOrderCode(event.getOrderCode());
                busInsuranceInfo.setCreatedBy(event.getCreatedBy());
                Long times = System.currentTimeMillis();
                busInsuranceInfo.setCreatedBy(times);
                busInsuranceInfo.setCreatedTime(times);
                busInsuranceInfos.add(busInsuranceInfo);

            }

           boolean success =  busInsuranceInfoService.insertBatch(busInsuranceInfos);
            if(success){
                baseResponse.setReturnCode(1000);
            }else {
                baseResponse.setReturnCode(1005);
                baseResponse.setMessage("保险信息保存失败");
            }
        } catch (Exception e) {
            baseResponse.setReturnCode(1005);
            baseResponse.setMessage("保险信息保存失败："+e.getMessage());
        }
        return baseResponse;
    }

    /**
     * 保险修改
     * @param event
     * @return
     */
    @EventHandler
    public BaseResponse handler(BusInsuranceInfoUpdateEvent event){
        BaseResponse baseResponse = new BaseResponse();
        Map<String,Object> mapParams = new HashMap<>();
        mapParams.put("ORDERCODE",event.getOrderNo());
        mapParams.put("CARDNO",event.getCardNo());
        List<BusInsuranceInfo> busInsuranceInfos = busInsuranceInfoService.selectByMap(mapParams);
        if(busInsuranceInfos !=null && busInsuranceInfos.size()>0){
            BusInsuranceInfo busInsuranceInfo = busInsuranceInfos.get(0);
            busInsuranceInfo.setStatus(event.getStatus());
            if(StringUtils.isNotBlank(event.getPolicyNo())){
                busInsuranceInfo.setPolicyNo(event.getPolicyNo());
            }
            if(StringUtils.isNotBlank(event.getCompany())){
                busInsuranceInfo.setCompany(event.getCompany());
            }
            if(event.getTermDate() !=null ){
                busInsuranceInfo.setTermDate(event.getTermDate());
            }
            if(event.getFromDate() !=null ){
                busInsuranceInfo.setFromDate(event.getFromDate());
            }
            if(event.getTime() !=null ){
                busInsuranceInfo.setTime(event.getTime());
            }
            boolean success = busInsuranceInfoService.insertOrUpdate(busInsuranceInfo);
            if(success){
                baseResponse.setReturnCode(1000);
            }else {
                baseResponse.setReturnCode(1005);
                baseResponse.setMessage("保险信息修改失败");
            }
        }else {
            baseResponse.setReturnCode(1005);
            baseResponse.setMessage("未查询到保险信息");
        }
        return baseResponse;
    }

}
