package com.vpclub.bait.busticket.query.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vpclub.bait.busticket.query.entity.BusCustomerInfo;
import com.vpclub.bait.busticket.query.entity.BusPassengerInfo;
import com.vpclub.bait.busticket.query.mapper.BusCustomerInfoMapper;
import com.vpclub.bait.busticket.query.mapper.BusPassengerInfoMapper;
import com.vpclub.bait.busticket.query.service.IBusCustomerInfoService;
import com.vpclub.bait.busticket.query.service.IBusPassengerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusPassengerInfoService extends ServiceImpl<BusPassengerInfoMapper,BusPassengerInfo> implements IBusPassengerInfoService<BusPassengerInfo> {

    public BusPassengerInfoService(){super();}
    public BusPassengerInfoService(BusPassengerInfoMapper  busPassengerInfoMapper){
        this.baseMapper = busPassengerInfoMapper;
    }
}
