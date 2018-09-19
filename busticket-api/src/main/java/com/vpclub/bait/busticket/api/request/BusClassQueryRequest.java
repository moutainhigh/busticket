package com.vpclub.bait.busticket.api.request;

import cn.vpclub.moses.utils.validator.annotations.NotEmpty;
import com.vpclub.bait.busticket.api.entity.BusClassInfo;
import com.vpclub.bait.busticket.api.entity.TimeInteoval;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  zengbaoyou
 * @E-mail:13678717317@139.com
 * @date 创建时间：2017年12月20日 上午17:25:56
 *
 */
@Data
@ApiModel("班次查询参数封装")
public class BusClassQueryRequest {
	@ApiModelProperty("出发地名称")
	@NotEmpty
	private String origin;
	@ApiModelProperty("目的地名称")
	@NotEmpty
	private String site;
	@ApiModelProperty("发班日期,格式为 YYYY-MM-DD")
	@NotEmpty
	private String date;
	@ApiModelProperty("查询时间段列表条件")
	private List<TimeInteoval> timeInteovals;
	@ApiModelProperty("发车客运站名称")
	private List<String> stations;
	@ApiModelProperty("是否只显示可售班次")
	private boolean statusFlag;
	@ApiModelProperty("车型")
	private List<String> carTypes;
}
