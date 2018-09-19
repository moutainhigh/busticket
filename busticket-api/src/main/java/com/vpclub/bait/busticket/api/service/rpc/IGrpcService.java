package com.vpclub.bait.busticket.api.service.rpc;

import com.vpclub.bait.busticket.api.entity.*;

import java.util.List;

public interface IGrpcService {
    public List<SiteInfo> getSiteInfos(String origin);
    public List<BusTicketOrderCode> queryByDateTimeAndUserId(String dateTime, String userId);
    public List<OrderInfo> queryByOrderIdAndUserId(String orderCode, String userId);
    public List<SensitiveWord> getAllensitiveWords();
    public BusStationMesInfo getStation(String origin, String stationName);

    public Boolean updateOrderStatus(Long id, Integer status);
}
