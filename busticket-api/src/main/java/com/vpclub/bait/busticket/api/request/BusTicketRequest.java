package com.vpclub.bait.busticket.api.request;

import cn.vpclub.moses.utils.validator.annotations.NotEmpty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;


/**
 *
 * @author  zengbaoyou
 * @E-mail:13678717317@139.com
 * @date 创建时间：2017年12月21日 上午17:23:56
 *
 */
@Data
@ApiModel("")
public class BusTicketRequest {
	@ApiModelProperty("发车客运站编码（必须）")
	@NotEmpty
	private String stationCode;
	@ApiModelProperty("班次号")
	@NotEmpty
	private String classCode;
	@ApiModelProperty("目的站")
	@NotEmpty
	private String site;
	@ApiModelProperty("出发日期")
	@NotEmpty
	private String date;
	@ApiModelProperty("票价")
	@NotEmpty
	private String price;
	@ApiModelProperty("票数")
	@NotEmpty
	private String ticket;
	@ApiModelProperty("旅客姓名")
	@NotEmpty
	private String name;
	@ApiModelProperty("旅客电话")
	@NotEmpty
	private String mobile;
	@ApiModelProperty("证件号")
	private String card;
	@ApiModelProperty("订单ID（由前台传入）")
	@NotEmpty
	private String orderId;
	@ApiModelProperty("保险参数")
	private Map<String,Object> ins;			//（如果不购买保险该参数不传）

	@ApiModelProperty("站点名称")
	private String stationName;			//（如果不购买保险该参数不传）

}
