package com.vpclub.bait.busticket.api.service;

import cn.vpclub.moses.core.model.response.BaseResponse;

public interface IRefundNoticeService {
    //汽车票退票通知
    public BaseResponse refundNotice(String orderCode,String json);
}
