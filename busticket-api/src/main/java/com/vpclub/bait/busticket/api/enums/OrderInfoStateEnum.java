package com.vpclub.bait.busticket.api.enums;

public enum OrderInfoStateEnum {
	SUCCESS,
	PENDING,
	CANCEL,
	HIDE;
	
	
	
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	public static Boolean contains(OrderInfoStateEnum jb)
	{
		for(OrderInfoStateEnum objType:OrderInfoStateEnum.values())
		{
			if(objType.equals(jb))
			{
				return true;
			}
		}
		return false;
	}
}
