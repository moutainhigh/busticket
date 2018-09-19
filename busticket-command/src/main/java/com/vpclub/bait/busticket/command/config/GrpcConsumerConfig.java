package com.vpclub.bait.busticket.command.config;

import cn.vpclub.spring.boot.grpc.annotations.GRpcClient;
import com.vpclub.bait.busticket.api.BusPassengerRpcServiceGrpc;
import com.vpclub.bait.busticket.api.BusStationInfoRpcServiceGrpc;
import com.vpclub.bait.busticket.api.BusTicketOrderCodeRpcServiceGrpc;
import com.vpclub.bait.busticket.api.SiteInfoRpcServiceGrpc;
import com.vpclub.bait.lifepayment.api.SensitiveWordInfoRpcServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class GrpcConsumerConfig {
    @GRpcClient("busticket-query")
    ManagedChannel busticketQueryChannel;

    @Bean
    public BusTicketOrderCodeRpcServiceGrpc.BusTicketOrderCodeRpcServiceBlockingStub getBlockingStub() {
        return BusTicketOrderCodeRpcServiceGrpc.newBlockingStub(busticketQueryChannel);
    }

    @Bean
    public SiteInfoRpcServiceGrpc.SiteInfoRpcServiceBlockingStub getSiteBlockingStub() {
        return SiteInfoRpcServiceGrpc.newBlockingStub(busticketQueryChannel);
    }

    @Bean
    public BusPassengerRpcServiceGrpc.BusPassengerRpcServiceBlockingStub getBusPassengerBlockingStub() {
        return BusPassengerRpcServiceGrpc.newBlockingStub(busticketQueryChannel);
    }

    @Bean
    public SensitiveWordInfoRpcServiceGrpc.SensitiveWordInfoRpcServiceBlockingStub getSensitiveWordInfoBlockingStub() {
        return SensitiveWordInfoRpcServiceGrpc.newBlockingStub(busticketQueryChannel);
    }
    @Bean
    public BusStationInfoRpcServiceGrpc.BusStationInfoRpcServiceBlockingStub getBusStationInfoBlockingStub() {
        return BusStationInfoRpcServiceGrpc.newBlockingStub(busticketQueryChannel);
    }
}
