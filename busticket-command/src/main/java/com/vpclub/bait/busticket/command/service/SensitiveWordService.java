package com.vpclub.bait.busticket.command.service;

import cn.vpclub.moses.core.model.response.BaseResponse;
import com.vpclub.bait.busticket.api.entity.SensitiveWord;
import com.vpclub.bait.busticket.api.util.RedisService;
import com.vpclub.bait.busticket.command.rpc.GrpcService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensitiveWordService {
	
	private Logger log= LoggerFactory.getLogger(SensitiveWordService.class);
	
	@Autowired
	private GrpcService grpcService;
	@Autowired
	private RedisService redisService;
//	@Autowired
	/*
	@SuppressWarnings("rawtypes")
	public Set<String> sensitiveWordFiltering(String text)
	{
		Set<String> set = null;
		try{
			// 初始化敏感词库对象
		    SensitiveWordInit sensitiveWordInit = new SensitiveWordInit();
		    List<SensitiveWord> sensitiveWords = sensitiveWordDao.getSensitiveWordListAll();
		   
		    // 构建敏感词库
		    Map sensitiveWordMap = sensitiveWordInit.initKeyWord(sensitiveWords);
		    // 传入SensitivewordEngine类中的敏感词库
		    SensitivewordEngine.sensitiveWordMap = sensitiveWordMap;
		    // 得到敏感词有哪些，传入2表示获取所有敏感词
		    set = SensitivewordEngine.getSensitiveWord(text, 2);
		}catch(Exception e){
			log.error("sensitiveWordFiltering Exception", e);
		}
	    return set;
	}*/
	
	/**
	 * 判断输入信息是否是敏感词，并且返回ReturnCode，返回null标识不是敏感词
	 * @param text  需要验证的输入信息
	 * @param tip   提示信息
	 * @return
	 */
	public BaseResponse sensitiveWordRet(String text, String tip){
		log.info("sensitiveWordRet : text =>" + text);
		if(StringUtils.isNotBlank(text) && isHasSensitive(text)){
			BaseResponse ret = new BaseResponse();
			ret.setReturnCode(1005);
			ret.setMessage(tip);
			return ret;
		}
		return null;
	}
	
	/**
	 * 判断输入的文本信息是否含有关键词
	 * @param text
	 * @return
	 */
	private boolean isHasSensitive(String text){
		boolean sensitiveFlag = false;
		try{
			List<SensitiveWord> contentList = getSensitiveContents();
			for (SensitiveWord sensitive : contentList){
				if(text.indexOf(sensitive.getContent()) != -1){
					log.info("isHasSensitive sensitiveException : text =>" + text + ",sensitive =>" + sensitive.getContent());
					return true;
				}
			}
		}catch(Exception e){
			return true;
		}
		return sensitiveFlag;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<SensitiveWord> getSensitiveContents() throws Exception{
		String redisKey = "EK_SENSITIVE_REDIS_KEY";
		List<SensitiveWord> contentList = redisService.redisGetList(redisKey, SensitiveWord.class);
		if(contentList == null || contentList.size()==0){
			contentList = grpcService.getAllensitiveWords();
			redisService.redisSetObj(redisKey, 3600, contentList);
		}
		return contentList;
	}
	
}