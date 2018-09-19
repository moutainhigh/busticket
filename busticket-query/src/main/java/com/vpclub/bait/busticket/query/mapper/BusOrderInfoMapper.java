package com.vpclub.bait.busticket.query.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.vpclub.bait.busticket.query.entity.BusCustomerInfo;
import com.vpclub.bait.busticket.query.entity.BusOrderInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BusOrderInfoMapper extends BaseMapper<BusOrderInfo> {
    List<Map<String,Object>> queryOrderCodeAndIdByUserId(@Param("dateTime") String dateTime, @Param("userId") String userId);
    List<BusOrderInfo> queryOrderCodeByOrderIdAndUserId(@Param("orderCode") String orderCode, @Param("userId") String userId);

}
