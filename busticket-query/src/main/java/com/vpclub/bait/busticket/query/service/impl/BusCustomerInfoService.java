package com.vpclub.bait.busticket.query.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vpclub.bait.busticket.query.entity.BusCustomerInfo;
import com.vpclub.bait.busticket.query.mapper.BusCustomerInfoMapper;
import com.vpclub.bait.busticket.query.service.IBusCustomerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusCustomerInfoService extends ServiceImpl<BusCustomerInfoMapper,BusCustomerInfo> implements IBusCustomerInfoService<BusCustomerInfo>{

    public BusCustomerInfoService(){super();}
    public BusCustomerInfoService(BusCustomerInfoMapper  busCustomerInfoMapper){
        this.baseMapper = busCustomerInfoMapper;
    }
}
