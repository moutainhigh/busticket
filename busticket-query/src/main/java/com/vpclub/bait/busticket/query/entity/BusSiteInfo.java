package com.vpclub.bait.busticket.query.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 存储在本地的订单详情
 */

@Data
@TableName("BUS_SITE_INFO")
public class BusSiteInfo extends Model<BusSiteInfo>{
    private Long id;
    private String stationCode;
    private String stationName;
    private String origin;
    private String site;
    private String itemCode;
    private Integer status;

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
