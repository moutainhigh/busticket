package com.vpclub.bait.busticket.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 存储在本地的订单详情
 */

@Data
public class OrderInfo{
    private Long id;
    private String classCode;
    private String site;
    private String stationCode;
    private Double price;
    private Double serviceAmount; //总服务费
    private Double insurAmount; //总保险费
    private Date classDate;
    private Integer ticket;
    private String name;
    private String mobile;
    private String card;
    /*@TableField("mall_orderId")
    private Long mallOrderId;*/
    private Integer status;
    private String origin;
    private String classTime;
    private String bookTime;
    private Double amount;
    private String orderCode;
    private String seatNo;
    private String userId;
    private String stationName;

    /**
     * 创建人
     */
    private Long createdBy;
    /**
     * 创建时间
     */
    private Long createdTime;
    /**
     * 更新人
     */
    private Long updatedBy;
    /**
     * 更新时间
     */
    private Long updatedTime;
    /**
     * 删除标识(1:在线; 2:删除)
     */
    private Integer deleted;
}
