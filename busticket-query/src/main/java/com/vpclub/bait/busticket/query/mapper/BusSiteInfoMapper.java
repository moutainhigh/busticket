package com.vpclub.bait.busticket.query.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.vpclub.bait.busticket.query.entity.BusCustomerInfo;
import com.vpclub.bait.busticket.query.entity.BusSiteInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusSiteInfoMapper extends BaseMapper<BusSiteInfo>{
    List<BusSiteInfo> querySiteByOrigin(@Param("origin") String origin);
}
