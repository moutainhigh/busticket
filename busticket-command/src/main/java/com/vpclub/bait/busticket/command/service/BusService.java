package com.vpclub.bait.busticket.command.service;

import cn.vpclub.moses.core.enums.ReturnCodeEnum;
import cn.vpclub.moses.core.model.response.BaseResponse;
import cn.vpclub.moses.utils.common.IdWorker;
import cn.vpclub.moses.utils.common.StringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.util.CollectionUtil;
import com.vpclub.bait.busticket.api.command.CreateBusCustomerInfoBatchCommand;
import com.vpclub.bait.busticket.api.command.CreateBusInsuranceInfoCommand;
import com.vpclub.bait.busticket.api.command.CreateBusOrderInfoCommand;
import com.vpclub.bait.busticket.api.command.UpdateBusInsuranceInfoCommand;
import com.vpclub.bait.busticket.api.config.InterfaceConfig;
import com.vpclub.bait.busticket.api.entity.*;
import com.vpclub.bait.busticket.api.interfaceresponse.BusTicketResponse;
import com.vpclub.bait.busticket.api.request.BusClassQueryRequest;
import com.vpclub.bait.busticket.api.request.BusTicketNewRequest;
import com.vpclub.bait.busticket.api.request.BusTicketRequest;
import com.vpclub.bait.busticket.api.request.InsuranceCallBackRequest;
import com.vpclub.bait.busticket.api.service.IBusService;
import com.vpclub.bait.busticket.api.service.IPaymentService;
import com.vpclub.bait.busticket.api.service.rpc.IGrpcService;
import com.vpclub.bait.busticket.api.util.PinyinUtil;
import com.vpclub.bait.busticket.api.util.RedisService;
import com.vpclub.bait.busticket.command.config.PayConfig;
import com.vpclub.bait.busticket.command.rpc.GrpcService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BusService implements IBusService {
    private static String BUS_ORIGIN_KEY = "bus.origin.rediskey";

    private static String BUS_SITE_KEY = "bus.site.rediskey";

    @Autowired
    BusServiceUtil busServiceUtil;

    @Autowired
    InterfaceConfig interfaceConfig;

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    IGrpcService grpcService;


    @Autowired
    PayConfig payConfig;

    @Autowired
    IPaymentService paymentService;

    @Autowired
    RedisService redisService;

    @Override
    public void getBusOrderPay(String orderId) {

    }

    @Override
    public BaseResponse getOrigin(String userId){
        BaseResponse ret=new BaseResponse();
        try{
//            validator.checkResource(UserInfo.class, 0, userId);
            String redisJson = redisService.redisGet(BUS_ORIGIN_KEY);
            if(StringUtils.isNotBlank(redisJson)){
                log.info("getOrigin get redisJson=>" + redisJson);
                ObjectMapper om=new ObjectMapper();
                ret =om.readValue(redisJson, BaseResponse.class);
                return ret;
            }

            String token= busServiceUtil.getTokenString();

            String resultJson=busServiceUtil.queryOrigin(token);

            JSONObject jsonObject = JSONObject.fromObject(resultJson);

            String reason = busServiceUtil.getErrorInfo(jsonObject,"getOrigin");
            if(StringUtils.isNotBlank(reason)){
                ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                ret.setMessage(reason);
                return ret;
            }else{
                JSONArray list= jsonObject.getJSONArray("originList");
                log.info("getOrigin get List<BusOrigin> list:"+list);

                //组装排序的起点城市
                List<ObjectOrdered> lists = new ArrayList<>();
                if(list!=null && list.size()>0){
                    for (int i=0;i<26;i++
                         ) {
                        String currentLetter = PinyinUtil.LETTERS[i];
                        ObjectOrdered<BusOrigin> objectOrdered = new ObjectOrdered<>();
                        objectOrdered.setIndex(currentLetter);
                        List<BusOrigin> busOrigins = new ArrayList<>();

                        for (int j = 0;j <list.size(); j++) {
                            BusOrigin busOrigin = new BusOrigin();
                            busOrigin.setOrigin(list.getJSONObject(j).getString("origin"));
                            busOrigin.setProvince(list.getJSONObject(j).getString("province"));
                            if(PinyinUtil.getPinYinHeadChar(busOrigin.getOrigin().split("")[0]).equals(currentLetter)){
                                busOrigins.add(busOrigin);
                            }
                        }
                        objectOrdered.setList(busOrigins);
                        if(busOrigins.size()!=0){
                            lists.add(objectOrdered);
                        }
                    }
                }
                ret.setDataInfo(lists);

                ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
                if(list != null && list.size() > 0){
                    JSONObject jsonObj = JSONObject.fromObject(ret);
                    redisJson = jsonObj.toString();
                    log.info("getOrigin set redisJson=>" + redisJson);
                    redisService.redisSet(BUS_ORIGIN_KEY, 3600, redisJson);
                }
            }
        }catch(Exception e){
            log.error("getOrigin Exception:" + e.getMessage(),e);
            ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
            ret.setMessage("汽车始发站查询失败，请稍后重试");
        }
        return ret;
    }
    @Override
    public BaseResponse returnBusOrder2(String userId){
        BaseResponse ret=new BaseResponse();

        try{
//            validator.checkResource(UserInfo.class, 0, userId);

            List<BusTicketOrderCode> list = null;//busServiceDao.selectObject(SqlUtil.BUS_ORDER_SELECT_BY_USERID, BusTicketOrderCode.class, userId);

            if(list.size()>0){
                List<BusTicketOrder> busOrders = new ArrayList<BusTicketOrder>();
                String token= busServiceUtil.getTokenString();
                for(BusTicketOrderCode busOrder : list){
                    String orderCode = busOrder.getOrderCode();
                    if(StringUtils.isNotBlank(orderCode)){
                        String resultJson=busServiceUtil.queryOrder(token,orderCode);
                        JSONObject jsonObject = JSONObject.fromObject(resultJson);
                        log.info("BusService returnBusOrder userId=>" + userId + " orderCode=>" + orderCode + " resultJson=>" + resultJson);
                        String reason = busServiceUtil.getErrorInfo(jsonObject,"returnBusOrder");
                        if(StringUtils.isBlank(reason)){
//							BusTicketOrder busTicketOrder=(BusTicketOrder) JSONObject.toBean(jsonObject, BusTicketOrder.class);
                            ObjectMapper om=new ObjectMapper();
                            BusTicketOrder busTicketOrder=om.readValue(jsonObject.toString(), BusTicketOrder.class);
                            log.info("BusService returnBusOrder userId=>" + userId + " BusTicketOrder=>" + busTicketOrder);
                            if("交易成功".equals(busTicketOrder.getStatus())){
                                String passWord = busServiceUtil.getDesVcode(busTicketOrder.getPassword());
                                String cardNo = busServiceUtil.getDesVcode(busTicketOrder.getCardNo());
                                busTicketOrder.setPassword(passWord);
                                busTicketOrder.setCardNo(cardNo);
                                // TODO: 2017/12/25
                                List<BusStationMesInfo> stations = null;//busServiceDao.selectObject(SqlUtil.BUS_STATION_QUERY, BusStationMesInfo.class, busTicketOrder.getOrigin(),busTicketOrder.getStationName());
                                log.info("BusService returnBusOrder origin="+busTicketOrder.getOrigin() +",stationName="+busTicketOrder.getStationName()+",stations =" +stations);
                                if(stations != null && stations.size()>0){
                                    BusStationMesInfo station = stations.get(0);
                                    String stationPhone = station.getStationPhone();
                                    if(StringUtils.isNotBlank(station.getStationAddress())){
                                        busTicketOrder.setStationAddress(station.getStationAddress());
                                    }
                                    if(StringUtils.isNotBlank(stationPhone)){
                                        List<String> stationPhones = new ArrayList<String>();
                                        String[] args = stationPhone.split(",");
                                        for(int i =0; i<args.length; i++){
                                            String phone = args[i];
                                            if(StringUtils.isNotBlank(phone)){
                                                stationPhones.add(phone);
                                            }
                                        }
                                        busTicketOrder.setStationPhones(stationPhones);
                                    }
                                }
                                List<InsuranceInfo> insuranceInfo = busTicketOrder.getInsuranceInfo();
                                if(insuranceInfo.size()>0){
                                    double price = insuranceInfo.get(0).getPrice()*0.01;
                                    busTicketOrder.setInsPrice(price+"");
                                    busTicketOrder.setInsNum(insuranceInfo.size()+"");
                                }else{
                                    busTicketOrder.setInsPrice("0");
                                    busTicketOrder.setInsNum("0");
                                }
                                busTicketOrder.setInsuranceInfo(null);
                                busOrders.add(busTicketOrder);
                            }
                            log.info("BusService BusOrderResponse BusTicketOrder=>" + busTicketOrder);
                        }
                    }
                }
                if(busOrders.size() > 0){
                    BusOrderDetailInfo busOrderDetail = new BusOrderDetailInfo();
                    busOrderDetail.setBusOrders(busOrders);
                    ret.setDataInfo(busOrderDetail);
                    ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            log.error("BusService returnBusOrder Exception:" + e.getMessage(),e);
            ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
            ret.setMessage("汽车票信息查询失败，请稍后重试");
        }

        return ret;
    }
    @Override
    public BaseResponse bookBusTickets(String userId,BusTicketRequest busTicketRequest){
        BaseResponse ret=new BaseResponse();

        try{
//            validator.checkResource(UserInfo.class, 0, userId);
//            validator.validateNULL(busTicketRequest);
            String vrfResult=busServiceUtil.verifyBusTicketReq(busTicketRequest);

            if(StringUtils.isBlank(vrfResult)){
                String token= busServiceUtil.getTokenString();

                if(busTicketRequest.getIns()!=null && busTicketRequest.getIns().get("cus")!=null){
                    String backUrl=interfaceConfig.getBackUrl();
                    busTicketRequest.getIns().put("backUrl",backUrl);
                }

                String resultJson = busServiceUtil.orderBook(token,busTicketRequest);
                JSONObject jsonObject = JSONObject.fromObject(resultJson);

                String reason = busServiceUtil.getErrorInfo(jsonObject,"bookBusTickets");
                if(StringUtils.isNotBlank(reason)){
                    ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                    ret.setMessage(reason);
                    return ret;
                }else{
                    BusTicketResponse busTicketResponse = (BusTicketResponse) JSONObject.toBean(jsonObject, BusTicketResponse.class);
                    log.info("BusService bookBusTickets getBusTicketResp busTicketResp:"+busTicketResponse);

                    //发送命令
                    CreateBusOrderInfoCommand createBusOrderInfoCommand = new CreateBusOrderInfoCommand();
                    createBusOrderInfoCommand.setBusTicketRequest(busTicketRequest);
                    createBusOrderInfoCommand.setBusTicketResponse(busTicketResponse);
                    createBusOrderInfoCommand.setUserId(userId);
                    createBusOrderInfoCommand.setId(IdWorker.getId());
                    commandGateway.sendAndWait(createBusOrderInfoCommand);

//                        int serialId=busServiceDao.insertOrderAndGetAutoIncreaseId(SqlUtil.BUS_ORDER_INFO_REQUEST_PARAM,userId,orderInfoList.get(0).getMall_orderId(),busTicketRequest,busTicketResponse);

                    Map<String,Object> ins=busTicketRequest.getIns();

                    if(ins != null && ins.get("cus") != null){
                        log.info("BusService insuranceInfo "+ins);
                        JSONArray array=JSONArray.fromObject(ins.get("cus"));
                        List<InsuranceCustomer> list=(List<InsuranceCustomer>) JSONArray.toList(array, InsuranceCustomer.class);
                        String price = (String) ins.get("price");
                        double insPrice=Double.parseDouble(price);
                        if(list!=null && list.size()>0) {
                            //发送命令，插入保险信息
                            CreateBusInsuranceInfoCommand createBusInsuranceInfoCommand = new CreateBusInsuranceInfoCommand();
                            createBusInsuranceInfoCommand.setInsuranceCustomers(list);
                            createBusInsuranceInfoCommand.setOrderCode(busTicketResponse.getOrderCode());
                            createBusInsuranceInfoCommand.setPrice(insPrice);
                            createBusInsuranceInfoCommand.setId(IdWorker.getId());
                            commandGateway.sendAndWait(createBusInsuranceInfoCommand);
                        }
                    }
                    BusTicketOrderCode busTicketOrderCode = new BusTicketOrderCode();
                    busTicketOrderCode.setOrderCode(busTicketResponse.getOrderCode());
                    ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
                    ret.setDataInfo(busTicketOrderCode);
                }
            }else{
                log.error("BusService bookBusTickets param busTicketReq(required) is null:" + vrfResult);
                ret.setReturnCode(ReturnCodeEnum.CODE_1006.getCode());
                ret.setMessage(vrfResult);
            }
        }catch(Exception e){
            log.error("bookBusTickets Exception:" + e.getMessage(),e);
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage("汽车票预定失败，请稍后重试");
        }

        return ret;
    }



    //汽车票获取车站列表
    @Override
    public BaseResponse getBusStation(String userId,String origin){
        BaseResponse ret = new BaseResponse();
        try{
            // 验证参数对象userId是否存在
//            validator.checkResource(UserInfo.class, 0, userId);

            if(StringUtils.isNotBlank(origin)){
                StationInfos stationInfos = new StationInfos();
                String tokenString = busServiceUtil.getTokenString();
                log.info("BusService getBusStation getTokenString tokenString:"+tokenString);
                String resultJson = busServiceUtil.queryStation(tokenString, origin);
                log.info("BusService getBusStation getResultJson resultJson:"+resultJson);
                JSONObject jsonObject = JSONObject.fromObject(resultJson);

                String reason = busServiceUtil.getErrorInfo(jsonObject,"getBusStation");
                if(StringUtils.isNotBlank(reason)){
                    ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                    ret.setMessage(reason);
                    return ret;
                }else{
                    List<StationInfo> list=(List<StationInfo>)jsonObject.get("stationList");
                    log.info("BusService getBusStation get List<StationInfo> list:"+list);
                    stationInfos.setStationList(list);
                    ret.setDataInfo(list);
                    ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
                }
            }else{
                log.error("BusService getBusStation Origin Not found");
                ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                ret.setMessage("查询站点所在州市不能为空");
            }
        }catch(Exception e){
            log.error("getBusStation Exception:" + e.getMessage(),e);
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage("汽车始发站列表获取失败，请稍后重试");
        }
        return ret;
    }

    //汽车票条件式筛选
    @Override
    public BaseResponse getBusClass(String userId,BusClassQueryRequest busClass){
        BaseResponse ret = new BaseResponse();
        ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
        try{
            // 验证参数对象userId是否存在
//            validator.checkResource(UserInfo.class, 0, userId);
            String busQuery = busServiceUtil.busClassIsEmipty(busClass);
            if(StringUtils.isBlank(busQuery)){
                String tokenString = busServiceUtil.getTokenString();
                log.info("BusService getBusClass getTokenString tokenString:"+tokenString);
                String resultJson = busServiceUtil.busClassQuery(tokenString, busClass.getOrigin(), busClass.getSite(), busClass.getDate());
                log.info("BusService getBusClass getResultJson resultJson:"+resultJson);
                JSONObject jsonObject = JSONObject.fromObject(resultJson);

                String reason = busServiceUtil.getErrorInfo(jsonObject,"getBusClass");
                if(StringUtils.isNotBlank(reason)){
                    ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                    ret.setMessage(reason);
                    return ret;
                }else{
                    BusClassList busClassList = busServiceUtil.selectBusClass(jsonObject, busClass);
                    if(StringUtil.isEmpty(busClassList) || CollectionUtils.isEmpty(busClassList.getClassList())){
                        ret.setMessage("无车次");
                        ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                    }
                    ret.setDataInfo(busClassList.getClassList());
                }
            }else{
                log.error("BusService getBusClass busClassIsBlank :" + busQuery);
                ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                ret.setMessage(busQuery);
            }
        }catch(Exception e){
            log.error("getBusClass Exception:" + e.getMessage(),e);
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage("汽车票班次信息查询失败，请稍后重试");
        }
        return ret;
    }

    //汽车票订单支付
    @Override
    public BaseResponse busOrderPay(String userId,String orderCode){
        BaseResponse ret = new BaseResponse();
        try{
            // 验证参数对象userId是否存在
//            validator.checkResource(UserInfo.class, 0, userId);
            if(StringUtils.isNotBlank(orderCode)){
                Object result = doBusOrderPay(userId,orderCode);
                if(result instanceof String){
                    ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                    ret.setMessage(result.toString());
                }else{
                    // TODO: 2017/12/25
//                    BusOrderPayInfo orderpayInfo =(BusOrderPayInfo) result;
                    ret.setDataInfo(null);
                }
            }else{
                ret.setReturnCode(ReturnCodeEnum.CODE_1006.getCode());
                ret.setMessage("订单编号不能为空");
                log.error("BusService orderPay orderCodeIsBlank :" + orderCode);
            }
        }catch(Exception e){
            log.error("busOrderPay Exception:" + e.getMessage(),e);
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage("汽车票预定失败，请稍后重试");
        }
        return ret;

    }

    /*public void getBusOrderPay(PayNotifyInfo payNotifyInfo){
        try{
            long orderId = payNotifyInfo.getOrderId();
            if(orderId>0){
                String BUS_ORDER_DO_PAY_SELECT = "select id,userId,orderCode,name,mobile,seatno,classtime,origin,site,stationName,classcode,card from Bus_Order_Info where mall_orderId=?";
                List<BusDoPayInfo> list = busServiceDao.selectObject(BUS_ORDER_DO_PAY_SELECT, BusDoPayInfo.class, orderId);
                if(list!=null && list.size()>0){
                    log.info("BusService  getBusOrderPay  List<BusDoPayInfo> list="+list + " orderId=>" + orderId);
                    BusDoPayInfo busDoPayInfo = list.get(0);
                    Object result = doBusOrderPay(busDoPayInfo.getUserId(),busDoPayInfo.getOrderCode());
                    log.info("BusService  getBusOrderPay  doBusOrderPayResult="+result);
                    if(result instanceof String){
                        busServiceUtil.ExceptionSendMessage("汽车票订票异常，请查看");
                    }else{
                        BusOrderPayInfo orderpayInfo =(BusOrderPayInfo) result;
                        String busStatus = orderpayInfo.getStatus();
                        int status = busServiceUtil.getBusOrderStatus(busStatus);
                        log.info("BusService  getBusOrderPay  status="+status);
                        if(status>0){
                            busServiceDao.update(SqlConstants.BUS_ORDER_DO_PAY_UPDATE_STATUS, status,busDoPayInfo.getUserId(),busDoPayInfo.getOrderCode());
                            String message = busServiceUtil.getBusMessage(busDoPayInfo, orderpayInfo.getPassword());
                            busServiceUtil.sendMessage(busDoPayInfo.getMobile(), message);
                        }else{
                            busServiceUtil.ExceptionSendMessage("汽车票订票异常，请查看");
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            log.error("BusService getBusOrderPay Exception:" + e.getMessage(),e);
        }
    }*/


    public Object doBusOrderPay(String userId,String orderCode) throws Exception{
        String tokenString = busServiceUtil.getTokenString();
        log.info("BusService doBusOrderPay getTokenString tokenString:"+tokenString);
        String resultJson = busServiceUtil.orderPay(tokenString,orderCode);
        log.info("BusService doBusOrderPay getResultJson resultJson:"+resultJson);
        JSONObject jsonObject = JSONObject.fromObject(resultJson);

        String reason = busServiceUtil.getErrorInfo(jsonObject,"busOrderPay");
        if(StringUtils.isNotBlank(reason)){
            return reason;
        }else{
            // TODO: 2017/12/25
//            BusOrderPayInfo orderpayInfo =(BusOrderPayInfo) JSONObject.toBean(jsonObject, BusOrderPayInfo.class);
            return null;
        }
    }

    //汽车票取消订单
    @Override
    public BaseResponse busOrderCancle(String userId,String orderCode,String type){
        BaseResponse ret = new BaseResponse();
        if(StringUtils.isBlank(orderCode)){
            log.error("busOrderCancle orderCodeException : userId =>" +userId + ",orderCode =>" + orderCode + ",type =>" + type);
            ret.setReturnCode(ReturnCodeEnum.CODE_1006.getCode());
            ret.setMessage("汽车票订单号不能为空！");
            return ret;
        }

        try{
            // 验证参数对象userId是否存在
//            validator.checkResource(UserInfo.class, 0, userId);

            //验证BusOrderCancleReq参数是否存在
            String busOrderCancle = busServiceUtil.OrderCancleJudge(orderCode,type);
            if(StringUtils.isBlank(busOrderCancle)){
                String tokenString = busServiceUtil.getTokenString();
                log.info("BusService busOrderCancle getTokenString tokenString:"+tokenString);
                String resultJson = busServiceUtil.orderCancel(tokenString,orderCode,type);
                log.info("BusService busOrderCancle getResultJson resultJson:"+resultJson);
                if(StringUtils.isBlank(resultJson)){
                    log.error("busOrderCancle resultNullException userId =>" + userId + ",Exception:" + resultJson);
                    ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                    ret.setMessage("取消订单失败！");
                    return ret;
                }

                JSONObject jsonObject = JSONObject.fromObject(resultJson);
                String reason = busServiceUtil.getErrorInfo(jsonObject,"busOrderCancle");
                if(StringUtils.isNotBlank(reason)){
                    log.error("busOrderCancle resultException : userId =>" +userId + ",orderCode =>" + orderCode + ",reason =>" + reason);
                    ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                    ret.setMessage(reason);
                }else{
                    OrderCancleInfo orderCancleInfo =(OrderCancleInfo) JSONObject.toBean(jsonObject, OrderCancleInfo.class);
                    ret.setDataInfo(orderCancleInfo);
                    ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
                }
            }else{
                log.error("BusService busOrderCancle busOrderCancleIsBlank userId =>" + userId + ",busOrderCancle =>" + busOrderCancle);
                ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                ret.setMessage(busOrderCancle);
            }
        }catch(Exception e){
            log.error("busOrderCancle userId =>" + userId + ",Exception:" + e.getMessage(),e);
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage("取消订单失败！");
        }
        return ret;
    }

    @Override
    public BaseResponse insuranceCallBack(InsuranceCallBackRequest insuranceCallBackRequest){
        BaseResponse ret = new BaseResponse();
        try{
            String orderNo = insuranceCallBackRequest.getOrderNo();

            List<InsuranceCallBackInfo> insuranceInfo = insuranceCallBackRequest.getInsuranceInfo();

            for(InsuranceCallBackInfo insurance :insuranceInfo){
                int status = 0;
                UpdateBusInsuranceInfoCommand updateBusInsuranceInfoCommand = new UpdateBusInsuranceInfoCommand();
                updateBusInsuranceInfoCommand.setOrderNo(orderNo);
                updateBusInsuranceInfoCommand.setCardNo(insurance.getCardNo());
                if("投保失败".equals(insurance.getStatus())){
                    status= -1;
                }else if("投保成功".equals(insurance.getStatus())){
                    status= 2;
                    updateBusInsuranceInfoCommand.setPolicyNo(insurance.getPolicyNo());
                    updateBusInsuranceInfoCommand.setCompany(insurance.getCompany());
                    String dateFormat[] = {"yyyy-mm-dd hh24:mi:ss"};
                    updateBusInsuranceInfoCommand.setTermDate(DateUtils.parseDate(insurance.getTermDate(),dateFormat));
                    updateBusInsuranceInfoCommand.setFromDate(DateUtils.parseDate(insurance.getFromDate(),dateFormat));
                    updateBusInsuranceInfoCommand.setTime(DateUtils.parseDate(insurance.getTime(),dateFormat));
                }
                updateBusInsuranceInfoCommand.setStatus(status);
                BaseResponse baseResponse = commandGateway.sendAndWait(updateBusInsuranceInfoCommand);
                log.info("BusService  insuranceCallBack 订单号="+orderNo +",证件号="+orderNo,insurance.getCardNo()+",更新结果:"+baseResponse.toString());
            }
            ret.setMessage("投保信息更新成功");
        }catch(Exception e){
            e.printStackTrace();
            log.error("insuranceCallBack Exception:" + e.getMessage(),e);
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage(e.getMessage());
        }
        return ret;
    }



    /*
     * 起始城市对应的终点城市列表
     */
    @Override
    public BaseResponse getSite(String userId,String origin) {
        BaseResponse ret = new BaseResponse();
        try {
            //验证参数对象userId是否存在
//            validator.checkResource(UserInfo.class, 0, userId);
            if(StringUtils.isNotBlank(origin)){
                // TODO: 2017/12/25
                String redisJson = redisService.redisGet(BUS_SITE_KEY);
                //log.info("redisJson:"+redisJson);
                if(StringUtils.isNotBlank(redisJson)){
//                    log.info("getSite get redisJson=>" + redisJson);
                    ObjectMapper om=new ObjectMapper();
                    ret =om.readValue(redisJson, BaseResponse.class);
                    return ret;
                }
                log.info("出缓存");
                List<SiteInfo> busSites = grpcService.getSiteInfos(origin);
                log.info("grpc查询数据了");
                //组装排序的起点城市
                List<ObjectOrdered> lists = new ArrayList<>();
                if(busSites!=null && busSites.size()>0){
                    for (int i=0;i<26;i++) {
                        String currentLetter = PinyinUtil.LETTERS[i];
                        ObjectOrdered<SiteInfo> objectOrdered = new ObjectOrdered<>();
                        objectOrdered.setIndex(currentLetter);
                        List<SiteInfo> busOrigins = new ArrayList<>();

                        for (int j = 0;j <busSites.size(); j++) {
                            SiteInfo siteInfo = busSites.get(j);
                            if(PinyinUtil.getPinYinHeadChar(siteInfo.getSite().split("")[0]).equals(currentLetter)){
                                busOrigins.add(siteInfo);
                            }
                        }
                        objectOrdered.setList(busOrigins);
                        if(busOrigins.size()!=0){
                            lists.add(objectOrdered);
                        }
                    }
                }

                ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
                ret.setMessage("");
                ret.setDataInfo(lists);

                if(lists!=null && lists.size()>0){
                    JSONObject jsonObj = JSONObject.fromObject(ret);
                    redisJson = jsonObj.toString();
                    redisService.redisSet(BUS_SITE_KEY,redisJson);
                }

            }else{
                ret.setReturnCode(ReturnCodeEnum.CODE_1006.getCode());
                ret.setMessage("参数不能为空!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("BusService getSite Exception :" + e.getMessage());
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage("查询汽车目的站点失败，请稍后重试");
        }
        return ret;
    }
    //订单列表
    @Override
    public BaseResponse returnBusOrder(String userId){
        BaseResponse ret=new BaseResponse();
        try{
            //验证userId是否存在
//            validator.checkResource(UserInfo.class, 0, userId);

            //使用GRPC获取列表
            List<BusTicketOrderCode> list = grpcService.queryByDateTimeAndUserId(busServiceUtil.getTimeHour(),userId);//busServiceDao.selectObject(busQuery.toString(), BusTicketOrderCode.class,busServiceUtil.getTimeHour(),userId);
            log.info("returnBusOrder orderQuery userId =>" + userId + ",List<BusTicketOrderCode> =>" + list);
            List<BusTicketOrder> busOrders = new ArrayList<BusTicketOrder>();
            if(list.size()>0){
                String token= busServiceUtil.getTokenString();
                for(BusTicketOrderCode busOrder : list){
                    String orderCode = busOrder.getOrderCode();
                    BusTicketOrder busTicketOrder = busServiceUtil.doReturnBusOrder(userId,token,orderCode);
                    if(busTicketOrder != null){
                        List<OrderInfo> list1 = grpcService.queryByOrderIdAndUserId(orderCode,userId);
                        busTicketOrder.setOrderId(busOrder.getOrderId());
                        if(list1!=null && list1.size()>0){
                            OrderInfo oi = list1.get(0);
                            busTicketOrder.setAmount(new BigDecimal(oi.getAmount()).add(new BigDecimal(oi.getServiceAmount())).doubleValue()+"");
                            busOrders.add(busTicketOrder);
                        }
                    }
                }
            }
            ret.setDataInfo(busOrders);
            ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
        }catch(Exception e){
            log.error("returnBusOrder Exception:" + e.getMessage(),e);
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage("汽车票查询失败，请稍后重试");
        }

        return ret;
    }


    /**
     * 根据出发城市和出发客运站查询触发客运站信息
     * @param origin
     * @param stationName
     * @return
     */
    public BusStationMesInfo getStationPhone(String origin,String stationName){
        /*BusServiceDao busServiceDao= new BusServiceDao();
        List<BusStationMesInfo> stations = busServiceDao.selectObject(SqlUtil.BUS_STATION_QUERY, BusStationMesInfo.class,origin,stationName);
        log.info("BusService returnBusOrder origin=>" + origin +",stationName =>"+ stationName + ",stations =>" +stations);
        if(stations != null && stations.size()>0){
            return stations.get(0);
        }*/
        return null;
    }


    //根据订单号查询汽车票订单详情
    @Override
    public BaseResponse busDetailQueryByOrderId(String userId,String orderCode){
        BaseResponse ret = new BaseResponse();
        try{
            //验证userId是否存在
//            validator.checkResource(UserInfo.class, 0, userId);
//            List<BusTicketOrderCode> list = grpcService.queryByOrderIdAndUserId(orderId,userId);
            String token= busServiceUtil.getTokenString();
            BusTicketOrder busTicket = busServiceUtil.doReturnBusOrder(userId,token,orderCode);
            List<OrderInfo> list1 = grpcService.queryByOrderIdAndUserId(orderCode,userId);
            if(list1!=null && list1.size()>0) {
                OrderInfo oi = list1.get(0);
                busTicket.setAmount(new BigDecimal(oi.getAmount()).add(new BigDecimal(oi.getInsurAmount())).add(new BigDecimal(oi.getServiceAmount())).doubleValue()+"");
            }
            log.info("busDetailQueryByOrderId BusTicketOrderResult->"+busTicket);
            if(busTicket != null){
                ret.setDataInfo(busTicket);
                ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
                return ret;
            }else{
                ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                ret.setMessage("车票暂未生成，请稍后查看");
            }
        }catch(Exception e){
            log.error("busDetailQueryByOrderId Exception ->"+e.getMessage(),e);
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage("汽车票查询失败，请稍后重试");
        }
        return ret;
    }

    @Override
    public BaseResponse placeAnOrder(String userId, BusTicketNewRequest busTicketNewRequest) {
        BaseResponse ret=new BaseResponse();

        try{
            // TODO: 2017/12/25
            /*validator.checkResource(UserInfo.class, 0, userId);
            validator.validateNULL(req);*/

            String vrfResult=busServiceUtil.verifyBusTicketNewReq(busTicketNewRequest);

            if(StringUtils.isBlank(vrfResult)){
                String token= busServiceUtil.getTokenString();
                String backUrl=interfaceConfig.getBackUrl();
                busTicketNewRequest.setInsBackUrl(backUrl);

                String resultJson=busServiceUtil.newOrderBook(token,busTicketNewRequest);
                JSONObject jsonObject = JSONObject.fromObject(resultJson);

                String reason = busServiceUtil.getErrorInfo(jsonObject,"bookBusTickets");
                if(StringUtils.isNotBlank(reason)){
                    ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                    ret.setMessage(reason);
                    return ret;
                }else{
                    BusTicketResponse busTicketResponse = (BusTicketResponse) JSONObject.toBean(jsonObject, BusTicketResponse.class);
                    log.info("BusService newbookBusTickets getBusTicketResp: "+busTicketResponse);

//                    List<OrderInfo> orderInfoList = orderInfoDAO.selectObject(SqlConstants.ORDERINFO_LIST_ONE, OrderInfo.class, userId,orderId);
                    BusTicketRequest busTicketRequest = new BusTicketRequest();
                    BeanUtils.copyProperties(busTicketNewRequest,busTicketRequest);
                    //发送命令,下单
                    CreateBusOrderInfoCommand createBusOrderInfoCommand = new CreateBusOrderInfoCommand();
                    createBusOrderInfoCommand.setBusTicketRequest(busTicketRequest);
                    createBusOrderInfoCommand.setBusTicketResponse(busTicketResponse);
                    createBusOrderInfoCommand.setUserId(userId);
                    createBusOrderInfoCommand.setServiceAmount(getFee(busTicketNewRequest).getServiceAmount().doubleValue());
                    createBusOrderInfoCommand.setInsurAmount(getFee(busTicketNewRequest).getInsurAmount().doubleValue());
                    createBusOrderInfoCommand.setId(IdWorker.getId());
                    commandGateway.sendAndWait(createBusOrderInfoCommand);


//                        String BUS_ORDER_INFO_REQUEST_PARAM="INSERT INTO BUS_ORDER_INFO (ID,CLASSCODE,SITE,STATIONCODE,PRICE,CLASSDATE,TICKET,NAME,MOBILE,CARD,MALL_ORDERID,STATUS,ORIGIN,CLASSTIME,BOOKTIME,AMOUNT,ORDERCODE,SEATNO,USERID,STATIONNAME) VALUES (BUS_ORDER_INFO_SEQ.NEXTVAL,?,?,?,?,to_date(?,'yyyy-mm-dd'),?,?,?,?,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi'),to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,?,?,?)";
//                        int i =busServiceDao.insertBusOrderAutoIncreaseId(BUS_ORDER_INFO_REQUEST_PARAM,userId,orderInfoList.get(0).getMall_orderId(),busTicketNewRequest,busTicketResponse);
                    log.info("insertBusOrderAutoIncreaseId userId=>"+userId + " busTicketResp=>" + busTicketResponse);

                    List<Customer> customers=busTicketNewRequest.getCustomers();
                    String orderCode = busTicketResponse.getOrderCode();

                    //发送命令插入客户信息
                    CreateBusCustomerInfoBatchCommand createBusCustomerInfoBatchCommand = new CreateBusCustomerInfoBatchCommand();
                    createBusCustomerInfoBatchCommand.setCustomers(customers);
                    createBusCustomerInfoBatchCommand.setOrderCode(orderCode);
                    createBusCustomerInfoBatchCommand.setId(IdWorker.getId());
                    commandGateway.sendAndWait(createBusCustomerInfoBatchCommand);
                    log.info("insertCustomerInfo userId=>"+userId + " customers=>" + customers);

                    double insPrice = Double.parseDouble(customers.get(0).getInsurancePrice());
                    if(insPrice > 0){
                        //发送命令，插入保险记录
                        List<InsuranceCustomer> list = new ArrayList<>();
                        for (Customer customer:customers
                                ) {
                            list.add(new InsuranceCustomer(customer.getCardNo(),customer.getName(),customer.getMobile()));
                        }
                        CreateBusInsuranceInfoCommand createBusInsuranceInfoCommand = new CreateBusInsuranceInfoCommand();
                        createBusInsuranceInfoCommand.setInsuranceCustomers(list);
                        createBusInsuranceInfoCommand.setOrderCode(orderCode);
                        createBusInsuranceInfoCommand.setPrice(insPrice);
                        createBusInsuranceInfoCommand.setId(IdWorker.getId());
                        commandGateway.sendAndWait(createBusInsuranceInfoCommand);
                    }
                    BusTicketOrderCode busTicketOrderCode = new BusTicketOrderCode();
                    busTicketOrderCode.setOrderCode(orderCode);
                    ret.setDataInfo(busTicketOrderCode);
                    //下单成功后直接发起支付请求
//                    ret = paymentService.payRequest(userId,orderCode);
                    ret = this.busDetailQueryByOrderId(userId,orderCode);
//                    createPaymentRequest(userId,busTicketNewRequest,busTicketResponse);
                }
            }else{
                log.error("newbookBusTickets param busTicketReq(required) is null:" + vrfResult);
                ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
                ret.setMessage(vrfResult);
            }
        }catch(Exception e){
            log.error("newbookBusTickets Exception:" + e.getMessage(),e);
            ret.setReturnCode(ReturnCodeEnum.CODE_1004.getCode());
            ret.setMessage("汽车票预定失败");
        }
        return ret;
    }

    /**
     * 费用计算（单位元）
     * @param request
     * @return
     */
    private Fee getFee(BusTicketNewRequest request){
        Fee fee = new Fee();
        //票数
        BigDecimal tickets = new BigDecimal(request.getTicket());
        String insurancePrice = request.getCustomers().get(0).getInsurancePrice();
        //单张保险费用
        BigDecimal insPrice = new BigDecimal("0");

        if(StringUtils.isNotBlank(insurancePrice)){
            insPrice = new BigDecimal(insurancePrice);
        }
        //票价
        BigDecimal price = new BigDecimal(request.getPrice());
        //单张服务费
        BigDecimal serviceRate = new BigDecimal(payConfig.getServiceRate());

        fee.setTotalAmount(price.multiply(tickets).add(insPrice.multiply(tickets)).add(serviceRate.multiply(tickets)));
        fee.setInsurAmount(insPrice.multiply(tickets));
        fee.setServiceAmount(serviceRate.multiply(tickets));
        return fee;
    }
    @Data
    public class Fee{
        BigDecimal totalAmount;
        BigDecimal insurAmount;
        BigDecimal serviceAmount;
    }
}
