package com.vpclub.bait.busticket.command.rpc;

import com.vpclub.bait.busticket.api.*;
import com.vpclub.bait.busticket.api.entity.*;
import com.vpclub.bait.busticket.api.service.rpc.IGrpcService;
import com.vpclub.bait.lifepayment.api.SensitiveWordInfoProto;
import com.vpclub.bait.lifepayment.api.SensitiveWordInfoRpcServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GrpcService implements IGrpcService{
    @Autowired
    BusTicketOrderCodeRpcServiceGrpc.BusTicketOrderCodeRpcServiceBlockingStub busTicketOrderCodeRpcServiceBlockingStub;

    @Autowired
    SiteInfoRpcServiceGrpc.SiteInfoRpcServiceBlockingStub siteInfoRpcServiceBlockingStub;

    @Autowired
    SensitiveWordInfoRpcServiceGrpc.SensitiveWordInfoRpcServiceBlockingStub sensitiveWordInfoRpcServiceBlockingStub;
    @Autowired
    BusStationInfoRpcServiceGrpc.BusStationInfoRpcServiceBlockingStub busStationInfoRpcServiceBlockingStub;
    /**
     * 通过GRPC根据起始站查询终点城市
     * @param origin
     * @return
     */
    public List<SiteInfo> getSiteInfos(String origin){
        List<SiteInfo> siteInfos = new ArrayList<>();
        SiteInfoProto.SiteInfoRequest request = SiteInfoProto.SiteInfoRequest.newBuilder().setOrigin(origin).build();
        SiteInfoProto.SiteInfoResponse response = siteInfoRpcServiceBlockingStub.query(request);
        String message = response.getMessage();
        if(StringUtils.isNotBlank(message)){
            JSONArray array = JSONArray.fromObject(response.getMessage());
            if(array !=null && array.size()>0){
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    SiteInfo info = new SiteInfo();
                    info.setSite(jsonObject.getString("site"));
                    siteInfos.add(info);
                }

            }
        }
        return siteInfos;
    }

    /**
     * 通过GRPC根据时间和用户id
     * @param dateTime 时间
     * @param userId 用户id
     * @return
     */
    public List<BusTicketOrderCode> queryByDateTimeAndUserId(String dateTime,String userId){
        List<BusTicketOrderCode> busTicketOrderCodes = new ArrayList<>();
        BusTicketOrderCodeProto.BusTicketOrderCodeRequest request = BusTicketOrderCodeProto.BusTicketOrderCodeRequest.newBuilder().setDateTime(dateTime).setUserId(userId).build();
        BusTicketOrderCodeProto.BusTicketOrderCodeResponse response = busTicketOrderCodeRpcServiceBlockingStub.queryByDateTimeAndUserId(request);
        JSONArray array = JSONArray.fromObject(response.getMessage());
        if(array !=null && array.size()>0){
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                BusTicketOrderCode busTicketOrderCode = new BusTicketOrderCode();
                busTicketOrderCode.setOrderCode(jsonObject.getString("ORDERCODE"));
//                busTicketOrderCode.setOrderId(jsonObject.getString("ORDERID"));
                busTicketOrderCodes.add(busTicketOrderCode);
            }

        }
        return busTicketOrderCodes;
    }

    /**
     * 通过GRPC根据时间和用户id
     * @param orderCode 第三方订单
     * @param userId 用户id
     * @return
     */
    public List<OrderInfo> queryByOrderIdAndUserId(String orderCode,String userId){
        List<OrderInfo> busTicketOrderCodes = new ArrayList<>();
        BusTicketOrderCodeProto.BusTicketOrderCodeRequest request = BusTicketOrderCodeProto.BusTicketOrderCodeRequest.newBuilder().setOrderCode(orderCode).setUserId(userId==null?"":userId).build();
        BusTicketOrderCodeProto.BusTicketOrderCodeResponse response = busTicketOrderCodeRpcServiceBlockingStub.queryByOrderIdAndUserId(request);
        JSONArray array = JSONArray.fromObject(response.getMessage());
        if(array !=null && array.size()>0){
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                OrderInfo orderInfo = (OrderInfo)JSONObject.toBean(jsonObject,OrderInfo.class);
                busTicketOrderCodes.add(orderInfo);
            }

        }
        return busTicketOrderCodes;
    }


    public List<SensitiveWord> getAllensitiveWords(){
        List<SensitiveWord> sensitiveWords = new ArrayList<>();
        SensitiveWordInfoProto.SensitiveWordInfoRequest request = SensitiveWordInfoProto.SensitiveWordInfoRequest.newBuilder().build();
        SensitiveWordInfoProto.SensitiveWordInfoResponse response = sensitiveWordInfoRpcServiceBlockingStub.queryAll(request);
        String message = response.getMessage();
        if(StringUtils.isNotBlank(message)){
            JSONArray array = JSONArray.fromObject(response.getMessage());
            if(array !=null && array.size()>0){
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    SensitiveWord info = new SensitiveWord();
                    info.setContent(jsonObject.getString("content"));
                    sensitiveWords.add(info);
                }

            }
        }
        return sensitiveWords;
    }

    /**
     *根据起始站点和站点名称查询站点信息
     * @param origin
     * @param stationName
     * @return
     */

    public BusStationMesInfo getStation(String origin,String stationName){
        List<BusStationMesInfo> busStationMesInfos = new ArrayList<>();
        BusStationInfoProto.BusStationInfoRequest request = BusStationInfoProto.BusStationInfoRequest.newBuilder().build();
        BusStationInfoProto.BusStationInfoResponse response = busStationInfoRpcServiceBlockingStub.queryByOriginAndStationName(request);
        String message = response.getMessage();
        if(StringUtils.isNotBlank(message)){
            JSONArray array = JSONArray.fromObject(response.getMessage());
            if(array !=null && array.size()>0){
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    BusStationMesInfo info = (BusStationMesInfo)JSONObject.toBean(jsonObject,BusStationMesInfo.class);
                    return info;
                }

            }
        }
        return null;
    }


    @Override
    public Boolean updateOrderStatus(Long id, Integer status) {
        BusTicketOrderCodeProto.BusTicketOrderCodeRequest request = BusTicketOrderCodeProto.BusTicketOrderCodeRequest.newBuilder().setId(id).setStatus(status).build();
        BusTicketOrderCodeProto.BusTicketOrderCodeResponse response = busTicketOrderCodeRpcServiceBlockingStub.updateOrderStatus(request);
        String message = response.getMessage();
        if(StringUtils.isNotBlank(message)){
            return Boolean.valueOf(message);
        }else {
            return Boolean.FALSE;
        }
    }
}
