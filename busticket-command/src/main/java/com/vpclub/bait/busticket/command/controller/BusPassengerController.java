package com.vpclub.bait.busticket.command.controller;

import cn.vpclub.moses.core.model.response.BaseResponse;
import com.vpclub.bait.busticket.api.entity.BusPassenger;
import com.vpclub.bait.busticket.command.service.BusPassengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 汽车票常用乘客信息
 * @author 张洪荣
 * @E-mail zhanghr@lianchuang.com
 * @date 2015年12月14日 下午2:39:59
 */

@RestController
//@RequestMapping("/erkuai")
public class BusPassengerController {
	private Logger log= LoggerFactory.getLogger(getClass());
	
	@Autowired
	private BusPassengerService busPassengerService;
	
	@RequestMapping(value="/{userId}/bus/passenger/create",method= RequestMethod.PUT)
	//创建乘客信息
	public BaseResponse createPassengerInfo(@PathVariable String userId, @RequestBody BusPassenger busPassenger,
											HttpServletRequest request){
		log.info("BusPassengerController createPassengerInfo params userId="+userId+",BusPassenger:"+busPassenger);
		BaseResponse ret=busPassengerService.createPassenger(userId,busPassenger);
		log.info("BusPassengerController createPassengerInfo ret:"+ret);
		return ret;
	}

	//查询乘客信息
	@RequestMapping(value="/{userId}/bus/passenger/query",method= RequestMethod.GET)
	public BaseResponse queryPassengerInfo(@PathVariable String userId, HttpServletRequest request){
		log.info("BusPassengerController queryPassengerInfo params userId="+userId);
		BaseResponse ret=busPassengerService.queryPassenger(userId);
		log.info("BusPassengerController queryPassengerInfo ret:"+ret);
		return ret;
	}

	//删除乘客信息
	@RequestMapping(value="/{userId}/bus/passenger/delete/{passengerId}",method= RequestMethod.DELETE)
	public BaseResponse deletePassengerInfo(@PathVariable String userId, @PathVariable Long passengerId, HttpServletRequest request){
		log.info("BusPassengerController deletePassengerInfo params userId="+userId+",passengerId:"+passengerId);
		BaseResponse ret=busPassengerService.deletePassenger(userId,passengerId);
		log.info("BusPassengerController deletePassengerInfo ret:"+ret);
		return ret;
	}
	//修改乘客信息
	@RequestMapping(value="/{userId}/bus/passenger/update",method= RequestMethod.POST)
	public BaseResponse updatePassengerInfo(@PathVariable String userId, @RequestBody BusPassenger busPassenger,
                                          HttpServletRequest request){
		log.info("BusPassengerController updatePassengerInfo params userId="+userId+",BusPassenger:"+busPassenger);
		BaseResponse ret=busPassengerService.updatePassenger(userId,busPassenger);
		log.info("BusPassengerController updatePassengerInfo ret:"+ret);
		return ret;
	}
	
}
