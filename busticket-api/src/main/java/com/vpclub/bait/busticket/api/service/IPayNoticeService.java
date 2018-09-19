package com.vpclub.bait.busticket.api.service;

import cn.vpclub.moses.core.model.response.BaseResponse;

public interface IPayNoticeService {
    //汽车票支付成功回调
    public BaseResponse payNotice(String orderCode,String json);
}
