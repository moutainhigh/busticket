package com.vpclub.bait.busticket.command.service;

import cn.vpclub.moses.core.enums.ReturnCodeEnum;
import cn.vpclub.moses.core.model.response.BaseResponse;
import cn.vpclub.moses.utils.common.IdWorker;
import com.vpclub.bait.busticket.api.entity.BusPassenger;
import com.vpclub.bait.busticket.command.rpc.BusPassengerGrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusPassengerService {
	private Logger log= LoggerFactory.getLogger(getClass());
	@Autowired
	private SensitiveWordService sensitiveWordService;
	
	@Autowired
	private BusPassengerGrpcService busPassengerGrpcService;
	
	public BaseResponse createPassenger(String userId,BusPassenger busPassenger){
		BaseResponse ret=new BaseResponse();
		
		try{
			List<BusPassenger> list = busPassengerGrpcService.queryByIdNumber(userId,busPassenger.getIdNumber());
			//判断乘客姓名是否含有敏感词汇
			BaseResponse sensitiveRet = sensitiveWordService.sensitiveWordRet(busPassenger.getPassengerName(),"乘客姓名违规");
			if(sensitiveRet != null){
				return sensitiveRet;
			}
        	

			
			if(list!=null && list.size()>0){
				log.info("BusPassengerService createPassenger：用户信息已存在");
				ret.setReturnCode(ReturnCodeEnum.CODE_1008.getCode());
				ret.setMessage("用户信息已存在");
			}else{
				busPassenger.setUserId(userId);
				busPassenger.setPassengerId(IdWorker.getId());
				boolean flag = busPassengerGrpcService.create(busPassenger);
				ret.setDataInfo(busPassenger.getPassengerId());
				ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
			}
		}catch(Exception e){
			log.error("createPassenger msg=>" + e.getMessage(),e);
			ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
			ret.setMessage(e.getMessage());
		}
		
		return ret;
	}
	
	public BaseResponse queryPassenger(String userId){
		BaseResponse ret=new BaseResponse();
		
		try{
			List<BusPassenger> list = busPassengerGrpcService.queryByuserId(userId);
			if(list!=null && list.size()>0){
				ret.setDataInfo(list);
				ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
			}
		}catch(Exception e){
			log.error("queryPassenger msg=>" + e.getMessage(),e);
			ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
			ret.setMessage(e.getMessage());
		}
		return ret;
	}
	
	public BaseResponse deletePassenger(String userId,Long passengerId){
		BaseResponse ret=new BaseResponse();
		
		try{

			boolean flag = busPassengerGrpcService.delete(passengerId);
			ret.setDataInfo(passengerId);
			ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
		}catch(Exception e){
			log.error("deletePassenger msg=>" + e.getMessage(),e);
			ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
			ret.setMessage(e.getMessage());
		}
		return ret;
	}
	
	public BaseResponse updatePassenger(String userId,BusPassenger busPassenger){
		BaseResponse ret=new BaseResponse();
		
		try{
			//判断乘客姓名是否含有敏感词汇
			BaseResponse sensitiveRet = sensitiveWordService.sensitiveWordRet(busPassenger.getPassengerName(),"乘客姓名违规");
			if(sensitiveRet != null){
				return sensitiveRet;
			}
			busPassenger.setUserId(userId);
			boolean flag = busPassengerGrpcService.update(busPassenger);
			ret.setDataInfo(busPassenger.getPassengerId());
			ret.setReturnCode(ReturnCodeEnum.CODE_1000.getCode());
		}catch(Exception e){
			log.error("updatePassenger msg=>" + e.getMessage(),e);
			ret.setReturnCode(ReturnCodeEnum.CODE_1005.getCode());
			ret.setMessage(e.getMessage());
		}
		return ret;
	}
	
}
