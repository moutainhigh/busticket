package com.vpclub.bait.busticket.api.service;

import cn.vpclub.moses.core.model.response.BaseResponse;

public interface IRefundService {
    //汽车票退票
    public BaseResponse refund(String userId, String orderCode);
}
