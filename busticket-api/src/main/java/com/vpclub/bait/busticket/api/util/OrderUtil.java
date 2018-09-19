package com.vpclub.bait.busticket.api.util;

public class OrderUtil {
    public static int getBusOrderStatus(String busStatus){
        int status = 0;
        if("预订".equals(busStatus)){
            status = 1;
        }else if("已支付".equals(busStatus)){
            status = 2;
        }else if("交易成功".equals(busStatus)){
            status = 3;
        }else if("交易关闭".equals(busStatus)){
            status = 4;
        }else if("退单".equals(busStatus)){
            status = 5;
        }
        return status;
    }
}
