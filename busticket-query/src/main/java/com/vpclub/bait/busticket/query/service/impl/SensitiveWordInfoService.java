package com.vpclub.bait.busticket.query.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vpclub.bait.busticket.query.entity.SensitiveWordInfo;
import com.vpclub.bait.busticket.query.mapper.SensitiveWordInfoMapper;
import com.vpclub.bait.busticket.query.service.ISensitiveWordInfoService;
import org.springframework.stereotype.Service;

@Service
public class SensitiveWordInfoService extends ServiceImpl<SensitiveWordInfoMapper,SensitiveWordInfo> implements ISensitiveWordInfoService<SensitiveWordInfo> {

    public SensitiveWordInfoService(){super();}
    public SensitiveWordInfoService(SensitiveWordInfoMapper  sensitiveWordInfoMapper){
        this.baseMapper = sensitiveWordInfoMapper;
    }
}