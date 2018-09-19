package com.vpclub.bait.busticket.query.rpc;

import cn.vpclub.moses.utils.grpc.GRpcMessageConverter;
import cn.vpclub.spring.boot.grpc.annotations.GRpcService;
import com.vpclub.bait.busticket.api.SiteInfoProto;
import com.vpclub.bait.busticket.api.SiteInfoRpcServiceGrpc;
import com.vpclub.bait.busticket.query.entity.BusSiteInfo;
import com.vpclub.bait.busticket.query.service.IBusSiteInfoService;
import com.vpclub.bait.busticket.query.service.impl.BusSiteInfoService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@GRpcService
@Slf4j
public class SiteInfoServiceImpl extends SiteInfoRpcServiceGrpc.SiteInfoRpcServiceImplBase {
    @Autowired
    IBusSiteInfoService busSiteInfoService;
    public SiteInfoServiceImpl(IBusSiteInfoService service) {
        this.busSiteInfoService = service;
    }
    @Override
    public void query(SiteInfoProto.SiteInfoRequest request, StreamObserver<SiteInfoProto.SiteInfoResponse> responseObserver) {
        Map<String,Object> mapParams = new HashMap<>();
        mapParams.put("ORIGIN",request.getOrigin());
        List<BusSiteInfo> list = busSiteInfoService.selectByMap(mapParams);
        SiteInfoProto.SiteInfoResponse response = SiteInfoProto.SiteInfoResponse.newBuilder().setMessage(JSONArray.fromObject(list).toString()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
