package com.vpclub.bait.busticket.query.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("BUS_PASSENGER_INFO")
public class BusPassengerInfo extends Model<BusPassengerInfo> {
    private String name;
    private String idNumber;
    private Long id;
    private String userId;
    private Integer orderValue;

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
        return id;
    }

}
