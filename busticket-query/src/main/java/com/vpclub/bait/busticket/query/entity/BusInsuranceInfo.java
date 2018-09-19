package com.vpclub.bait.busticket.query.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("BUS_INSURANCE_INFO")
public class BusInsuranceInfo {
    private Long id;
    private String cardNo;
    private String mobile;
    private String name;
    private String orderCode;
    private Double price;
    private Integer status;
    private String policyNo;
    private String company;
    private Date termDate;
    private Date fromDate;
    private Date time;
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
}
