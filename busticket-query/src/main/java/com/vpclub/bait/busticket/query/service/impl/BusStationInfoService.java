package com.vpclub.bait.busticket.query.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vpclub.bait.busticket.query.entity.BusCustomerInfo;
import com.vpclub.bait.busticket.query.entity.BusStationInfo;
import com.vpclub.bait.busticket.query.mapper.BusCustomerInfoMapper;
import com.vpclub.bait.busticket.query.mapper.BusStationInfoMapper;
import com.vpclub.bait.busticket.query.service.IBusCustomerInfoService;
import com.vpclub.bait.busticket.query.service.IBusStationInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusStationInfoService extends ServiceImpl<BusStationInfoMapper,BusStationInfo> implements IBusStationInfoService<BusStationInfo> {

    public BusStationInfoService(){super();}
    public BusStationInfoService(BusStationInfoMapper  busStationInfoMapper){
        this.baseMapper = busStationInfoMapper;
    }
}
