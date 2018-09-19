package com.vpclub.bait.busticket.query.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

/**
 * 存储在本地的订单详情
 */

@Data
@TableName("BUS_ORDER_INFO")
public class BusOrderInfo extends Model<BusOrderInfo>{
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
    @TableField("created_by")
    private Long createdBy;
    /**
     * 创建时间
     */
    @TableField("created_time")
    private Long createdTime;
    /**
     * 更新人
     */
    @TableField("updated_by")
    private Long updatedBy;
    /**
     * 更新时间
     */
    @TableField("updated_time")
    private Long updatedTime;
    /**
     * 删除标识(1:在线; 2:删除)
     */
    private Integer deleted;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
