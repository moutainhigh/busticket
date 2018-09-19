package com.vpclub.bait.busticket.query.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vpclub.bait.busticket.query.entity.BusCustomerInfo;
import com.vpclub.bait.busticket.query.entity.BusInsuranceInfo;
import com.vpclub.bait.busticket.query.mapper.BusCustomerInfoMapper;
import com.vpclub.bait.busticket.query.mapper.BusInsuranceInfoMapper;
import com.vpclub.bait.busticket.query.service.IBusCustomerInfoService;
import com.vpclub.bait.busticket.query.service.IBusInsuranceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusInsuranceInfoService extends ServiceImpl<BusInsuranceInfoMapper,BusInsuranceInfo> implements IBusInsuranceInfoService<BusInsuranceInfo>{

    public BusInsuranceInfoService(){super();}
    public BusInsuranceInfoService(BusInsuranceInfoMapper  busInsuranceInfoMapper){
        this.baseMapper = busInsuranceInfoMapper;
    }
}
