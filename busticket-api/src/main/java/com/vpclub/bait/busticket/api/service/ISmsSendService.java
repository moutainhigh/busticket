package com.vpclub.bait.busticket.api.service;

public interface ISmsSendService {
    boolean sendMessage(String userId, String serviceId, String password, String paramsString);
}
