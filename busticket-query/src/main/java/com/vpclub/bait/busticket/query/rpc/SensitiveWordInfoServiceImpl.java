package com.vpclub.bait.busticket.query.rpc;

import cn.vpclub.spring.boot.grpc.annotations.GRpcService;
import com.vpclub.bait.busticket.query.entity.SensitiveWordInfo;
import com.vpclub.bait.busticket.query.service.ISensitiveWordInfoService;
import com.vpclub.bait.lifepayment.api.SensitiveWordInfoProto;
import com.vpclub.bait.lifepayment.api.SensitiveWordInfoRpcServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GRpcService
@Slf4j
public class SensitiveWordInfoServiceImpl extends SensitiveWordInfoRpcServiceGrpc.SensitiveWordInfoRpcServiceImplBase {
    @Autowired
    ISensitiveWordInfoService sensitiveWordInfoService;
    public SensitiveWordInfoServiceImpl(ISensitiveWordInfoService service) {
        this.sensitiveWordInfoService = service;
    }
    @Override
    public void queryAll(SensitiveWordInfoProto.SensitiveWordInfoRequest request, StreamObserver<SensitiveWordInfoProto.SensitiveWordInfoResponse> responseObserver) {
        Map<String,Object> mapParams = new HashMap<>();
        List<SensitiveWordInfo> list = sensitiveWordInfoService.selectByMap(mapParams);
        SensitiveWordInfoProto.SensitiveWordInfoResponse response = SensitiveWordInfoProto.SensitiveWordInfoResponse.newBuilder().setMessage(JSONArray.fromObject(list).toString()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
