package com.vpclub.bait.busticket.api.entity;

/**
 * 客运汽车站点查询返回参数
 * @author 曾保友
 * @E-mail 13678717317@
 * @date 2015年12月8日 下午5:06:48
 */

public class BusOrigin {
	private String origin;
	private String province;
	
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	
	@Override
	public String toString() {
		return "BusOrigin [origin=" + origin + ", province=" + province + "]";
	}
	
}
