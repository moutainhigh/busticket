package com.vpclub.bait.busticket.query.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vpclub.bait.busticket.query.entity.BusInsuranceInfo;
import com.vpclub.bait.busticket.query.entity.BusSiteInfo;
import com.vpclub.bait.busticket.query.mapper.BusInsuranceInfoMapper;
import com.vpclub.bait.busticket.query.mapper.BusSiteInfoMapper;
import com.vpclub.bait.busticket.query.service.IBusInsuranceInfoService;
import com.vpclub.bait.busticket.query.service.IBusSiteInfoService;
import org.springframework.stereotype.Service;

@Service
public class BusSiteInfoService extends ServiceImpl<BusSiteInfoMapper,BusSiteInfo> implements IBusSiteInfoService<BusSiteInfo> {

    public BusSiteInfoService(){super();}
    public BusSiteInfoService(BusSiteInfoMapper  busSiteInfoMapper){
        this.baseMapper = busSiteInfoMapper;
    }
}