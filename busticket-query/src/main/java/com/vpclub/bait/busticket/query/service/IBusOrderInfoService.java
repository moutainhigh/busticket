package com.vpclub.bait.busticket.query.service;

import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Map;

public interface IBusOrderInfoService <BusOrderInfo> extends IService<BusOrderInfo> {
    List<Map<String,Object>> queryOrderCodeAndIdByUserId(String dateTime, String userId);
    List<BusOrderInfo> queryOrderCodeByOrderIdAndUserId(String orderCode, String userId);
}
