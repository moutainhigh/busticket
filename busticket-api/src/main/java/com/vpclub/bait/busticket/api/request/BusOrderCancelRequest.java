package com.vpclub.bait.busticket.api.request;

import cn.vpclub.moses.utils.validator.annotations.NotEmpty;
import cn.vpclub.moses.utils.validator.annotations.NotNull;
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
@ApiModel(description="订单处理参数封装")
public class BusOrderCancelRequest {
	@ApiModelProperty("下单成功的下单编号")
	@NotEmpty
	private String orderCode;
	@ApiModelProperty("操作类型：0 取消订单；1 作废订单；2 退单。")
	@NotEmpty
	private String type;
}
