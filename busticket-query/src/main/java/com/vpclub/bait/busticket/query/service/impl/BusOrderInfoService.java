package com.vpclub.bait.busticket.query.service.impl;

import cn.vpclub.moses.core.model.response.BaseResponse;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vpclub.bait.busticket.query.entity.BusCustomerInfo;
import com.vpclub.bait.busticket.query.entity.BusOrderInfo;
import com.vpclub.bait.busticket.query.mapper.BusCustomerInfoMapper;
import com.vpclub.bait.busticket.query.mapper.BusOrderInfoMapper;
import com.vpclub.bait.busticket.query.service.IBusCustomerInfoService;
import com.vpclub.bait.busticket.query.service.IBusOrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BusOrderInfoService extends ServiceImpl<BusOrderInfoMapper,BusOrderInfo> implements IBusOrderInfoService<BusOrderInfo> {
    public BusOrderInfoService(){super();}
    public BusOrderInfoService(BusOrderInfoMapper  busOrderInfoMapper){
        this.baseMapper = busOrderInfoMapper;
    }

    @Override
    public List<Map<String, Object>> queryOrderCodeAndIdByUserId(String dateTime, String userId) {
        return baseMapper.queryOrderCodeAndIdByUserId(dateTime,userId);
    }

    @Override
    public List<BusOrderInfo> queryOrderCodeByOrderIdAndUserId(String orderCode, String userId) {
        return baseMapper.queryOrderCodeByOrderIdAndUserId(orderCode,userId);
    }
}
