package com.vpclub.bait.busticket.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author  zengbaoyou
 * @E-mail:13678717317@139.com
 * @date 创建时间：2017年12月20日 上午17:23:56
 *
 */
@Data
@ApiModel("班次时间间隔封装")
public class TimeInteoval {
	@ApiModelProperty("开始的小时数")
	private int startHour;
	@ApiModelProperty("结束的小时数")
	private int endHour;

}
