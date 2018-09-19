package com.vpclub.bait.busticket.api.service;

import cn.vpclub.moses.core.model.response.BaseResponse;

public interface IPaymentService {
    //汽车票发起支付请求
    public BaseResponse payRequest(String userId, String orderCode,String frontUrl);
}
