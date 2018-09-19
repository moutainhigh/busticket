package com.vpclub.bait.busticket.query.rpc;


import cn.vpclub.spring.boot.grpc.annotations.GRpcService;
import com.vpclub.bait.busticket.api.BusPassengerProto;
import com.vpclub.bait.busticket.api.BusPassengerRpcServiceGrpc;
import com.vpclub.bait.busticket.query.entity.BusPassengerInfo;
import com.vpclub.bait.busticket.query.service.IBusPassengerInfoService;
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
public class BusPassengerInfoServiceImpl extends BusPassengerRpcServiceGrpc.BusPassengerRpcServiceImplBase{
    @Autowired
    private IBusPassengerInfoService busPassengerInfoService;
    public BusPassengerInfoServiceImpl(IBusPassengerInfoService service) {
        this.busPassengerInfoService = service;
    }
    @Override
    public void queryByIdNumber(BusPassengerProto.BusPassengerRequest request, StreamObserver<BusPassengerProto.BusPassengerResponse> responseObserver) {
        Map<String,Object> mapParams = new HashMap<>();
        mapParams.put("idnumber",request.getIdNumber());
        mapParams.put("userid",request.getUserId());
        List<BusPassengerInfo> lists = busPassengerInfoService.selectByMap(mapParams);
        BusPassengerProto.BusPassengerResponse response = BusPassengerProto.BusPassengerResponse.newBuilder().setMessage(JSONArray.fromObject(lists).toString()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void queryByUserId(BusPassengerProto.BusPassengerRequest request, StreamObserver<BusPassengerProto.BusPassengerResponse> responseObserver) {
        Map<String,Object> mapParams = new HashMap<>();
        mapParams.put("userid",request.getUserId());
        List<BusPassengerInfo> lists = busPassengerInfoService.selectByMap(mapParams);
        BusPassengerProto.BusPassengerResponse response = BusPassengerProto.BusPassengerResponse.newBuilder().setMessage(JSONArray.fromObject(lists).toString()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    @Override
    public void create(BusPassengerProto.BusPassengerRequest request, StreamObserver<BusPassengerProto.BusPassengerResponse> responseObserver) {
        BusPassengerInfo info = new BusPassengerInfo();
        BeanUtils.copyProperties(request,info);
        boolean flag = busPassengerInfoService.insert(info);
        BusPassengerProto.BusPassengerResponse response = BusPassengerProto.BusPassengerResponse.newBuilder().setMessage(String.valueOf(flag)).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void delete(BusPassengerProto.BusPassengerRequest request, StreamObserver<BusPassengerProto.BusPassengerResponse> responseObserver) {
        boolean flag = busPassengerInfoService.deleteById(request.getId());
        BusPassengerProto.BusPassengerResponse response = BusPassengerProto.BusPassengerResponse.newBuilder().setMessage(String.valueOf(flag)).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void update(BusPassengerProto.BusPassengerRequest request, StreamObserver<BusPassengerProto.BusPassengerResponse> responseObserver) {
        BusPassengerInfo busPassengerInfo = (BusPassengerInfo)busPassengerInfoService.selectById(request.getId());
        busPassengerInfo.setName(request.getName());
        busPassengerInfo.setOrderValue(request.getOrderValue());
        busPassengerInfo.setUpdatedTime(new Date().getTime());
        busPassengerInfo.setIdNumber(request.getIdNumber());
        boolean flag = busPassengerInfoService.updateById(busPassengerInfo);
        BusPassengerProto.BusPassengerResponse response = BusPassengerProto.BusPassengerResponse.newBuilder().setMessage(String.valueOf(flag)).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
