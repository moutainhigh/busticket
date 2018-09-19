package com.vpclub.bait.busticket.api.entity;

import java.util.List;

/** 
 * 
 * @author  杨敏[QQ:1045144279]
 * @E-mail:18387163231@139.com
 * @date 创建时间：2015年12月10日 上午10:52:42 
 * 
 */
public class BusClassList {
	private List<BusClassInfo> classList;           //客运站列表

	public List<BusClassInfo> getClassList() {
		return classList;
	}

	public void setClassList(List<BusClassInfo> classList) {
		this.classList = classList;
	}

	@Override
	public String toString() {
		return "BusClassList [classList=" + classList + "]";
	}
	
	
	
}
