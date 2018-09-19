package com.vpclub.bait.busticket.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 支付请求结果
 */
@Data
public class PayRequestResult implements Serializable{
    private String payUrl;
    private String sessionId;
}
