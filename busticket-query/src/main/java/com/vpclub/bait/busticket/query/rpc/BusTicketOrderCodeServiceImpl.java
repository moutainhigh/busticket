package com.vpclub.bait.busticket.query.rpc;

import cn.vpclub.spring.boot.grpc.annotations.GRpcService;
import com.vpclub.bait.busticket.api.BusTicketOrderCodeProto;
import com.vpclub.bait.busticket.api.BusTicketOrderCodeRpcServiceGrpc;
import com.vpclub.bait.busticket.query.entity.BusOrderInfo;
import com.vpclub.bait.busticket.query.service.IBusOrderInfoService;
import com.vpclub.bait.busticket.query.service.ISensitiveWordInfoService;
import com.vpclub.bait.busticket.query.service.impl.BusOrderInfoService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
@GRpcService
@Slf4j
public class BusTicketOrderCodeServiceImpl extends BusTicketOrderCodeRpcServiceGrpc.BusTicketOrderCodeRpcServiceImplBase {
    @Autowired
    IBusOrderInfoService busOrderInfoService;
    public BusTicketOrderCodeServiceImpl(IBusOrderInfoService service) {
        this.busOrderInfoService = service;
    }
    @Override
    public void queryByDateTimeAndUserId(BusTicketOrderCodeProto.BusTicketOrderCodeRequest request, StreamObserver<BusTicketOrderCodeProto.BusTicketOrderCodeResponse> responseObserver) {
        List<Map<String,Object>> lists = busOrderInfoService.queryOrderCodeAndIdByUserId(request.getDateTime(),request.getUserId());
        BusTicketOrderCodeProto.BusTicketOrderCodeResponse response = BusTicketOrderCodeProto.BusTicketOrderCodeResponse.newBuilder().setMessage(JSONArray.fromObject(lists).toString()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void queryByOrderIdAndUserId(BusTicketOrderCodeProto.BusTicketOrderCodeRequest request, StreamObserver<BusTicketOrderCodeProto.BusTicketOrderCodeResponse> responseObserver) {
        List<BusOrderInfo> lists = busOrderInfoService.queryOrderCodeByOrderIdAndUserId(request.getOrderCode(),request.getUserId());
        BusTicketOrderCodeProto.BusTicketOrderCodeResponse response = BusTicketOrderCodeProto.BusTicketOrderCodeResponse.newBuilder().setMessage(JSONArray.fromObject(lists).toString()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateOrderStatus(BusTicketOrderCodeProto.BusTicketOrderCodeRequest request, StreamObserver<BusTicketOrderCodeProto.BusTicketOrderCodeResponse> responseObserver) {
        BusOrderInfo info = (BusOrderInfo)busOrderInfoService.selectById(request.getId());
        info.setStatus(request.getStatus());
        boolean flag = busOrderInfoService.updateById(info);
        BusTicketOrderCodeProto.BusTicketOrderCodeResponse response = BusTicketOrderCodeProto.BusTicketOrderCodeResponse.newBuilder().setMessage(flag+"").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public ServerServiceDefinition bindService() {
        return super.bindService();
    }
}
