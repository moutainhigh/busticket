package com.vpclub.bait.busticket.api.enums;

public enum OrderStatus {
    PRE_DETERMINE("预订",1),
    HAS_PAYED("已支付",2),
    SUCCESS_TRADE("交易成功",3),
    TRADE_CLOSE("交易关闭",4),
    REFUNDED("退单",5);

    private String name;
    private int value;

    private OrderStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
