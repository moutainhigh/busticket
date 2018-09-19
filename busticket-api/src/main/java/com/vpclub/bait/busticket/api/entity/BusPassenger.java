package com.vpclub.bait.busticket.api.entity;

import java.sql.Timestamp;

/**
 * 汽车票乘客信息查询响应
 * @author 张洪荣
 * @E-mail zhanghr@lianchuang.com
 * @date 2015年12月14日 下午4:37:17
 */

public class BusPassenger {
	private String passengerName;
	private String idNumber;
	private Long passengerId;
	private String userId;
	private Integer orderValue;
	private Timestamp updateTime;
	
	public String getPassengerName() {
		return passengerName;
	}
	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public Long getPassengerId() {
		return passengerId;
	}
	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Integer getOrderValue() {
		return orderValue;
	}
	public void setOrderValue(Integer orderValue) {
		this.orderValue = orderValue;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	
	@Override
	public String toString() {
		return "BusPassenger [passengerName=" + passengerName + ", idNumber="
				+ idNumber + ", passengerId=" + passengerId + ", userId="
				+ userId + ", orderValue=" + orderValue + ", updateTime="
				+ updateTime + "]";
	}
	
	
}
