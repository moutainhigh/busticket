package com.vpclub.bait.busticket.query.rpc;


import cn.vpclub.spring.boot.grpc.annotations.GRpcService;
import com.vpclub.bait.busticket.api.BusStationInfoProto;
import com.vpclub.bait.busticket.api.BusStationInfoRpcServiceGrpc;
import com.vpclub.bait.busticket.query.entity.BusStationInfo;
import com.vpclub.bait.busticket.query.service.IBusStationInfoService;
import com.vpclub.bait.busticket.query.service.ISensitiveWordInfoService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@GRpcService
@Slf4j
public class BusStationInfoServiceImpl extends BusStationInfoRpcServiceGrpc.BusStationInfoRpcServiceImplBase{
    @Autowired
    private IBusStationInfoService busStationInfoService;
    public BusStationInfoServiceImpl(IBusStationInfoService service) {
        this.busStationInfoService = service;
    }
    @Override
    public void queryByOriginAndStationName(BusStationInfoProto.BusStationInfoRequest request, StreamObserver<BusStationInfoProto.BusStationInfoResponse> responseObserver) {
        Map<String,Object> mapParams = new HashMap<>();
        mapParams.put("origin",request.getOrigin());
        mapParams.put("stationName",request.getStationName());
        List<BusStationInfo> lists = busStationInfoService.selectByMap(mapParams);
        BusStationInfoProto.BusStationInfoResponse response = BusStationInfoProto.BusStationInfoResponse.newBuilder().setMessage(JSONArray.fromObject(lists).toString()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
