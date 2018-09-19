package com.vpclub.bait.busticket.command.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vpclub.bait.busticket.api.config.InterfaceConfig;
import com.vpclub.bait.busticket.api.entity.*;
import com.vpclub.bait.busticket.api.request.BusClassQueryRequest;
import com.vpclub.bait.busticket.api.request.BusTicketNewRequest;
import com.vpclub.bait.busticket.api.request.BusTicketRequest;
import com.vpclub.bait.busticket.api.service.ISmsSendService;
import com.vpclub.bait.busticket.api.service.rpc.IGrpcService;
import com.vpclub.bait.busticket.api.util.DESUtil;
import com.vpclub.bait.busticket.api.util.Encode;
import com.vpclub.bait.busticket.api.util.RedisService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class BusServiceUtil {

	@Autowired
	InterfaceConfig interfaceConfig;

	@Autowired
	IGrpcService grpcService;

	@Autowired
	RedisService redisService;

	@Autowired
	ISmsSendService smsSendService;

	private static RSAPublicKey publicKey;
	private static RSAPrivateKey privateKey;
	private static String tokenKey = "redisKey.bus.tokenKey";
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private int CONNECTION_TIMEOUT = 15000;

	@PostConstruct
	private void init(){
		String keyFilePath = interfaceConfig.getKeyFilepath();
		log.info("getSignKey keyFilePath=>" + keyFilePath);
		try {
			getSignKey(keyFilePath);
		} catch (Exception e) {
			log.error("getSignKey Exception:" + e.getMessage(),e);
		}

	}
	
	
	//汽车票预订或得token
	public String getTokenString() throws Exception{
		if(interfaceConfig.getIsTest() == 1 ){
			return getTokenStringTest();
		}else{
			return getTokenStringDev();
		}
	}
	//汽车票预订或得token  测试
	public String getTokenStringTest() throws Exception{
		String test_token = auth();
		log.info("BusService getTokenStringTest token=>" + test_token);
		return test_token;
	}

	//汽车票预订或得token  实际部署
	public String getTokenStringDev() throws Exception{
		redisService.redisDel(tokenKey);
		String token = redisService.redisGet(tokenKey);
		if(StringUtils.isBlank(token)){
			token = updateToken();
		}
		return token;
	}
	
	//更新汽车票token
	private String updateToken() throws Exception{
		String token = "";
		int count = 0;
		while(count < 3 && StringUtils.isBlank(token)){
			token = auth();
			if(StringUtils.isNotBlank(token)){
				redisService.redisSet(tokenKey, token);
			}
			count++;
			log.info("BusService getTokenStringDev token=>" + token + " count=>" + count);
		}
		if(StringUtils.isBlank(token)){
			throw new Exception("Cant Get BusService Token");
		}
		return token;
	}
	

	
	//根据城市查询汽车票站点，向车票方发送请求
	public String queryStation(String token, String origin) throws Exception {	
    	String stationPath = interfaceConfig.getHost()+interfaceConfig.getStation();
        // 构建参数
        NameValuePair[] params = new NameValuePair[]{
                new NameValuePair("origin", origin)
        };
        // 调用 get 请求
        String resultJson = get(token,stationPath , StringUtils.EMPTY, params);
        log.info("queryStation token=>" + token + " origin=>" + origin + " ResponseJson=>" + resultJson);
        return resultJson;
    }

	//根据出发城市、目的地城市、时间来获取车次信息
	public String busClassQuery(String token, String origin, String site, String date) throws Exception{
		String classes=interfaceConfig.getClasses();
		String host=interfaceConfig.getHost();
		String classPath = interfaceConfig.getHost()+interfaceConfig.getClasses();
		log.info("classPath"+classPath);
		// 构建参数
        NameValuePair[] params = new NameValuePair[]{
                new NameValuePair("origin", origin),
                new NameValuePair("site", site),
                new NameValuePair("date", date)
        };
        String pi=params.toString();
        log.info("params :"+pi);
        // 调用 get 请求
        String resultJson = get(token, classPath, StringUtils.EMPTY, params);
//        log.info("busClassQuery token=>" + token + " origin=>" + origin + " site=>" + site + " date=>" + date + " ResponseJson=>" + resultJson);
        return resultJson;
	}

	//取消订单
	public String orderCancel(String token, String orderCode, String type) throws Exception {
		String orderCancle = interfaceConfig.getHost()+interfaceConfig.getOrder();
        NameValuePair[] params = new NameValuePair[]{                    // 构建参数
                new NameValuePair("type", type)
        };
        String sign =sign(orderCode + token);                             // 对数据进行签名
        String resultJson = delete(token, orderCancle, orderCode, params, sign);  // 调用 delete 请求
        return resultJson;
    }

	//订单支付
	public String orderPay(String token, String orderCode) throws Exception {
		String orderPay = interfaceConfig.getHost()+interfaceConfig.getOrder();
        NameValuePair[] params = new NameValuePair[]{                            // 构建参数
                new NameValuePair("orderCode", orderCode)
        };
        String sign = sign(orderCode + token);                                   // 对数据进行签名

        String resultJson = put(token, orderPay, orderCode, params, StringUtils.EMPTY, sign);    // 调用 put 请求
        log.info("busClassQuery token=>" + token + " orderCode=>" + orderCode + " ResponseJson=>" + resultJson);
        return resultJson;
    }

	 //汽车票下单
  	public String orderBook(String token,BusTicketRequest busTicketRequest) throws Exception{
  		log.info("orderBook token=>" + token + " busTicketReq=>" + busTicketRequest);
  		String busOrderUrl=interfaceConfig.getHost()+interfaceConfig.getOrder();

  		JSONObject jsonObject=JSONObject.fromObject(busTicketRequest);
  		String data= jsonObject.toString();

  		String stationCode=busTicketRequest.getStationCode();
  		String classCode=busTicketRequest.getClassCode();
  		String site=busTicketRequest.getSite();
  		String date=busTicketRequest.getDate();
  		String price=busTicketRequest.getPrice();
  		String ticket=busTicketRequest.getTicket();
  		String mobile=busTicketRequest.getMobile();
  		String card=busTicketRequest.getCard();

  		String sign = sign(stationCode+classCode+site+date+price+ticket+mobile+card+token);
  		String result=post(token,busOrderUrl, StringUtils.EMPTY,data,sign);
  		log.info("orderBook ResponseJson=>" + result);
  		return result;
  	}

  	 //新汽车票下单
  	public String newOrderBook(String token,BusTicketNewRequest busTicketNewRequest) throws Exception{
  		log.info("newOrderBook token=>" + token + " BusTicketNewReq=>" + busTicketNewRequest);
  		String busOrderUrl=interfaceConfig.getHost()+interfaceConfig.getOrder();
  		//设置保险回调地址
		busTicketNewRequest.setInsBackUrl(interfaceConfig.getBackUrl());
  		JSONObject jsonObject=JSONObject.fromObject(busTicketNewRequest);
  		String data= jsonObject.toString();

  		String stationCode=busTicketNewRequest.getStationCode();
  		String classCode=busTicketNewRequest.getClassCode();
  		String site=busTicketNewRequest.getSite();
  		String date=busTicketNewRequest.getDate();
  		String price=busTicketNewRequest.getPrice();
  		String ticket=busTicketNewRequest.getTicket();
  		String mobile=busTicketNewRequest.getMobile();
  		String card=busTicketNewRequest.getCard();

  		String sign = sign(stationCode+classCode+site+date+price+ticket+mobile+card+token);
  		String result=post(token,busOrderUrl, StringUtils.EMPTY,data,sign);
  		log.info("newOrderBook ResponseJson=>" + result);
  		return result;
  	}


	//查询出发站点
	public String queryOrigin(String token) throws Exception{
		String busOriginUrl=interfaceConfig.getHost()+interfaceConfig.getOrigin();
		NameValuePair[] params=new NameValuePair[]{};

		//get请求
		log.info("queryOrigin token{} and ResponseJson{}",token,busOriginUrl);
		String result=get(token,busOriginUrl, StringUtils.EMPTY,params);
		log.info("queryOrigin ResponseJson=>" + result);
		return result;
	}

	//查询汽车票订单
	public String queryOrder(String token,String orderCode) throws Exception{
		log.info("queryOrder token=>" + token + " orderCode=>" + orderCode);
		String busOrderUrl=interfaceConfig.getHost()+interfaceConfig.getOrder();
		NameValuePair[] params=new NameValuePair[]{};

		//get请求
		String result=get(token,busOrderUrl,orderCode,params);
		log.info("queryOrder ResponseJson=>" + result);
		return result;
	}

	//验证，获取token
    public String auth() throws Exception {

        String bus_username=interfaceConfig.getUsername();
		String bus_key=interfaceConfig.getKey();
        String bus_session = interfaceConfig.getHost() + interfaceConfig.getSession();
        Map<String, String> body = new HashMap<String, String>();

        String timestamp = String.valueOf(System.currentTimeMillis());            // 获取当前时间戳

        String[] args = {bus_username, bus_key, timestamp};                       // 构造签名哈希

        Arrays.sort(args);                                                        // 进行排序

        body.put("username", bus_username);
        body.put("signature", DigestUtils.sha1Hex(StringUtils.join(args)));
        body.put("timestamp", timestamp);

        Gson gson = new Gson();
        // 将数据序列化
        String data = gson.toJson(body);

        // 调用 post 请求
        String resultJson = post(null, bus_session, StringUtils.EMPTY, data, StringUtils.EMPTY);
        log.info("auth ResponseJson=>" + resultJson);
        // 将结果反序列化
        Map result = gson.fromJson(resultJson, Map.class);

        log.info("auth Map=>" + result);
        String token = "";
        if(result.containsKey("token")){
        	token = result.get("token").toString();
        }
        log.info("auth token=>" + token);
        return token;
    }

    //加载公钥和私钥
    protected void loadPEMKey(InputStream public_data, InputStream private_data) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        if (public_data != null) {
            try {
                LineIterator public_li = IOUtils.lineIterator(public_data, "utf-8");

                StringBuilder public_sb = new StringBuilder();
                while (public_li.hasNext()) {
                    String line = public_li.next();
                    if (!line.startsWith("-")) {
                        public_sb.append(line).append("\n");
                    }
                }
                X509EncodedKeySpec public_keySpec = new X509EncodedKeySpec(Base64.decodeBase64(public_sb.toString()));
                publicKey = (RSAPublicKey) keyFactory.generatePublic(public_keySpec);
                log.info("加载公钥成功");
            } catch (InvalidKeySpecException e) {
            	log.info("公钥非法");
                throw new Exception("公钥非法");
            } catch (IOException e) {
            	log.info("公钥数据内容读取错误");
                throw new Exception("公钥数据内容读取错误");
            } catch (NullPointerException e) {
            	log.info("公钥数据为空");
                throw new Exception("公钥数据为空");
            }
        }

        if (private_data != null) {
            try {
                LineIterator private_li = IOUtils.lineIterator(private_data, "utf-8");

                StringBuilder private_sb = new StringBuilder();
                while (private_li.hasNext()) {
                    String line = private_li.next();
                    if (!line.startsWith("-")) {
                        private_sb.append(line).append("\n");
                    }
                }

                PKCS8EncodedKeySpec private_keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(private_sb.toString()));
                privateKey = (RSAPrivateKey) keyFactory.generatePrivate(private_keySpec);
                log.info("加载私钥成功");
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                log.info("私钥非法");
                throw new Exception("私钥非法");
            } catch (IOException e) {
            	log.info("私钥数据内容读取错误");
                throw new Exception("私钥数据内容读取错误");
            } catch (NullPointerException e) {
            	log.info("私钥数据为空");
                throw new Exception("私钥数据为空");
            }
        }
    }

    //加载公钥和私钥
    protected void loadKey(InputStream public_data, InputStream private_data) throws Exception {
        if (public_data != null) {
            ObjectInputStream public_input = new ObjectInputStream(public_data);
            publicKey = (RSAPublicKey) public_input.readObject();
        }

        if (private_data != null) {
            ObjectInputStream private_input = new ObjectInputStream(private_data);
            privateKey = (RSAPrivateKey) private_input.readObject();
        }
    }

    //加载公匙文件
    private void getSignKey(String privateKey) throws Exception{
    	InputStream private_data = null;
        int mod = 0;

        if (StringUtils.isNotBlank(privateKey)) {
            if (privateKey.endsWith(".dat")) {
                mod = 1;
            }
            if (privateKey.endsWith(".pem")) {
                mod = 2;
            }
            private_data = Encode.class.getResourceAsStream(privateKey);

//            File file = new File(privateKey);
//            private_data = new FileInputStream(file);
        }

        if (mod == 1) {
            loadKey(null, private_data);
        }
        if (mod == 2) {
            loadPEMKey(null, private_data);
        }
    }

    //数据签名
    public String sign(String data) throws Exception {
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes("utf-8"));
        return Base64.encodeBase64String(signature.sign());
    }

    //发送post请求
    //发送get请求

	private String get(String token, String url, String sub, NameValuePair[] pairs) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        String result = "";
        GetMethod method = new GetMethod(url + sub);
        try{
             method.addRequestHeader("Authorization", token);
             method.setQueryString(pairs);
             HttpClient client = new HttpClient();
            String queryString = method.getQueryString();
            String path = method.getPath();
            String host = method.getURI().getHost();
            log.info("token-->{}",token);
            log.info("URL-->{}", "http://"+host+path+"?"+queryString);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
            client.executeMethod(method);
			 log.info("statusCode："+client.executeMethod(method	));
			 log.info("输出反馈结果 :"+method.getResponseBodyAsStream());
             result = IOUtils.toString(method.getResponseBodyAsStream(), "utf-8");
        }catch(Exception e){
        	log.error("发送get请求查询 bus get Exception ：", e);
        }finally{
        	method.releaseConnection();
        }
		log.info("发送get方法车次信息返回的result"+result);
        return result;
    }
	private String post(String token, String url, String sub, String data, String sign) throws Exception {
		if (!url.endsWith("/")) {
			url = url + "/";
		}
		String result = "";
		PostMethod method = new PostMethod(url + sub);
		try{
			RequestEntity entity = new StringRequestEntity(data, "application/json", "utf-8");
			method.setRequestEntity(entity);
			method.addRequestHeader("Authorization", token);
			method.addRequestHeader("Sign", sign);
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
			client.executeMethod(method);
			result = IOUtils.toString(method.getResponseBodyAsStream(), "utf-8");
		}catch(Exception e){
			log.error("bus post Exception", e);
		}finally{
			method.releaseConnection();
		}
		log.info("发送post方法车次信息返回的result"+result);
		return result;

	}

    //发送delete请求
    private String delete(String token, String url, String sub, NameValuePair[] pairs, String sign) throws Exception {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        String result = "";
        DeleteMethod method = new DeleteMethod(url + sub);
        try{
        	 method.setQueryString(pairs);
             method.addRequestHeader("Authorization", token);
             method.addRequestHeader("Sign", sign);
             HttpClient client = new HttpClient();
             client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
             client.executeMethod(method);
             result = IOUtils.toString(method.getResponseBodyAsStream(), "utf-8");
        }catch(Exception e){
        	log.error("bus delete Exception", e);
        }finally{
        	method.releaseConnection();
        }
        return result;
    }

   //发送put请求
    private String put(String token, String url, String sub, NameValuePair[] pairs, String data, String sign) throws Exception {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        String result = "";
        PutMethod method = new PutMethod(url + sub);
        try{
//            method.getinterfaceConfig.getHost()interfaceConfiguration().setProxy("10.168.57.241",8088);
            RequestEntity entity = new StringRequestEntity(data, "application/json", "utf-8");
            method.setRequestEntity(entity);
            method.setQueryString(pairs);
            method.addRequestHeader("Authorization", token);
            method.addRequestHeader("Sign", sign);
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
            client.executeMethod(method);
            result = IOUtils.toString(method.getResponseBodyAsStream(), "utf-8");
        }catch(Exception e){
        	log.error("bus put Exception", e);
        }finally{
        	method.releaseConnection();
        }
        return result;
    }
    
    
    //判断参数对象BusClassQueryReq的参数是否为空
    public String busClassIsEmipty(BusClassQueryRequest busClass){
    	String busQuery = "";
    	if(StringUtils.isBlank(busClass.getOrigin())){
    		busQuery = "出发地点不能为空";
    	}else if(StringUtils.isBlank(busClass.getSite())){
    		busQuery = "目的地点不能为空";
    	}else if(StringUtils.isBlank(busClass.getDate())){
    		busQuery = "发车日期不能为空";
    	}
    	return busQuery;
    }
    
    //根据传入的JSONObject对象进行条件筛选并转换为实体类对象
    public BusClassList selectBusClass(JSONObject jsonObject, BusClassQueryRequest busClassQueryRequest) throws ParseException{
    	BusClassList busClass = new BusClassList();
    	List<BusClassInfo> busClassList = new ArrayList<BusClassInfo>();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    	
    	List<String> stationList = busClassQueryRequest.getStations();
    	List<TimeInteoval> timeList = busClassQueryRequest.getTimeInteovals();
    	List<String> carTypeList = getCarTypeName(busClassQueryRequest.getCarTypes());
    	
    	JSONArray array = jsonObject.getJSONArray("classList");
    	for(int i=0;i<array.size();i++){
    		JSONObject obj =array.getJSONObject(i);
    		if(busClassQueryRequest.isStatusFlag()){
    			if(!"正常".equals(obj.getString("status"))){
    				continue;
    			}
    		}
    		if(stationList!=null && stationList.size()>0){
    			if(!stationList.contains(obj.getString("stationCode"))){
    				continue;
    			}
    		}
    		if(carTypeList!=null && carTypeList.size()>0){
    			if(!carTypeList.contains(obj.getString("carType"))){
    				continue;
    			}
    		}
    		if(timeList!=null && timeList.size()>0){
    			String time = obj.getString("useTime");
    			Date date = sdf.parse(time);
				int hour = date.getHours();
				int minute = date.getMinutes();
    			boolean flag = false;
    			for(TimeInteoval timeInte : timeList){
    				boolean timeJudge = hour<timeInte.getEndHour() || (hour==timeInte.getEndHour() && minute==0);
    				if(timeInte.getStartHour()<=hour && timeJudge){
    					flag = true;
    				}
    			}
    			if(!flag){
    				continue;
    			}
    		}
    		
    		BusClassInfo busClassInfo = (BusClassInfo) JSONObject.toBean(obj, BusClassInfo.class);
			caculateArrivalTime(busClassInfo);//计算预计到达时间
    		busClassList.add(busClassInfo);
    	}
    	System.out.println(busClassList.size());
    	busClass.setClassList(busClassList);
    	return busClass;
    }
    
    private List<String> getCarTypeName(List<String> carTypeList){
    	List<String> nameList = null;
     	if(carTypeList != null && carTypeList.size() > 0){
     		nameList = new ArrayList<String>();
    		for(String carType : carTypeList){
        		if("w".equals(carType)){
        			nameList.add("卧铺");
        		}else if("z".equals(carType)){
        			nameList.add("坐席");
        		}
        	}
    	}
    	return nameList;
    }
    
    //验证汽车票请求参数
    public String verifyBusTicketReq(BusTicketRequest busTicketRequest){
    	String requestParam = "";
    	if(StringUtils.isBlank(busTicketRequest.getStationCode())){
    		requestParam = "发车客运站编码不能为空";
    	}else if(StringUtils.isBlank(busTicketRequest.getClassCode())){
    		requestParam = "班次号不能为空";
    	}else if(StringUtils.isBlank(busTicketRequest.getSite())){
    		requestParam = "目的站不能为空";
    	}else if(StringUtils.isBlank(busTicketRequest.getDate())){
    		requestParam = "出发日期不能为空";
    	}else if(StringUtils.isBlank(busTicketRequest.getPrice())){
    		requestParam = "票价不能为空";
    	}else if(StringUtils.isBlank(busTicketRequest.getTicket())){
    		requestParam = "票数不能为空";
    	}else if(StringUtils.isBlank(busTicketRequest.getMobile())){
    		requestParam = "旅客电话不能为空";
    	}else if(StringUtils.isBlank(busTicketRequest.getCard())){
    		requestParam = "旅客证件号不能为空";
    	}else if(StringUtils.isBlank(busTicketRequest.getName())){
    		requestParam = "旅客姓名不能为空";
    	}
    	
    	return requestParam;
    }
    
  //验证汽车票请求参数
  public String verifyBusTicketNewReq(BusTicketNewRequest busTicketNewRequest){
    	String requestParam = "";
    	if(StringUtils.isBlank(busTicketNewRequest.getStationCode())){
    		requestParam = "发车客运站编码不能为空";
    	}else if(StringUtils.isBlank(busTicketNewRequest.getClassCode())){
    		requestParam = "班次号不能为空";
    	}else if(StringUtils.isBlank(busTicketNewRequest.getSite())){
    		requestParam = "目的站不能为空";
    	}else if(StringUtils.isBlank(busTicketNewRequest.getDate())){
    		requestParam = "出发日期不能为空";
    	}else if(StringUtils.isBlank(busTicketNewRequest.getPrice())){
    		requestParam = "票价不能为空";
    	}else if(StringUtils.isBlank(busTicketNewRequest.getTicket())){
    		requestParam = "票数不能为空";
    	}else if(StringUtils.isBlank(busTicketNewRequest.getName())){
    		requestParam = "联系人姓名不能为空";
    	}else if(StringUtils.isBlank(busTicketNewRequest.getMobile())){
    		requestParam = "旅客电话不能为空";
    	}else if(StringUtils.isBlank(busTicketNewRequest.getCard())){
    		requestParam = "旅客证件号不能为空";
    	}else if(busTicketNewRequest.getCustomers() == null){
    		requestParam = "旅客信息不能为空";
    	}
    	
    	return requestParam;
    }

    
    
    //取消订单判断参数对象是否为空
    public String OrderCancleJudge(String orderCode,String type){
    	String orderCancleJudge = "";
    	if(StringUtils.isBlank(orderCode)){
    		orderCancleJudge = "订单编号不能为空";
    	}else if(StringUtils.isBlank(type)){
    		orderCancleJudge = "订单操作类型不能为空";
    	}
    	return orderCancleJudge;
    }
    

    
    /*
	 * 将获得的汽车票密码des加密
	 */
	public String getDesVcode(String VcodeRes) throws Exception{
		String Vcode = "";
		String des_key = interfaceConfig.getDesKey();
		Vcode = DESUtil.base64AndDesEncode(VcodeRes,"UTF-8",des_key);               //将或得的汽车票密码以MD5的方式编码
		return Vcode;
	}
    
	/**
	 * 发送移动短信
	 * @param userId  手机号码
	 * @param message 短信信息
	 * @return
	 */
	public void sendMessage(String userId, String message){
		try{
			String paramString = getParamString(message);
			smsSendService.sendMessage(userId,"001561","commonerkuai",paramString);
			// TODO: 2017/12/21
		}catch(Exception e){
			log.error("sendMessage Exception userId =>" + userId + ",message =>" + message + ",Exception =>" + e.getMessage(),e);
		}

//		String url = ErkuaiinterfaceConfigUtil.getSendMessageUrl();
//		String result = "";
//		try {
//			StringBuffer messageSb = new StringBuffer();
//			messageSb.append("sn=hzeksh");
//			messageSb.append("&pwd=" + Md5Util.MD5("hzeksheksh#ipi"));
//			messageSb.append("&mobile=" + userId);
//			messageSb.append("&content=" + URLEncoder.encode(message, "gbk"));
//
//			result = Common.getRestful(url, messageSb.toString(), "POST","application/x-www-form-urlencoded","");
//
//		} catch (Exception e) {
//			log.error("sendMessage Exception userId =>" + userId + ",message =>" + message + ",Exception =>" + e.getMessage(),e);
//		}
//		log.info("sendMessage Result userId =>" + userId + ",message =>" + message + ",result=>" + result);
//		return result;
	}
	
	/**
	 * 拼装短信信息
	 * @param orderInfo
	 * @return
	 */
	public String getBusMessage(OrderInfo orderInfo, String password){
		StringBuffer message = new StringBuffer();
		message.append("您已成功购买");
		message.append(getMessageTime(orderInfo.getClassTime()));                        //时间
		message.append(",【");
		message.append(orderInfo.getOrigin());
		if(StringUtils.isNotBlank(orderInfo.getStationName())){
			message.append(orderInfo.getStationName());
		}
		message.append("-" + orderInfo.getSite());
		message.append("】【车次：").append(orderInfo.getClassCode());                        //车次
		message.append("】的汽车票，【座号：").append(orderInfo.getSeatNo());                     //座位号
		message.append("】，购票人:").append(orderInfo.getName());                            //姓名
		message.append(",身份证号：").append(orderInfo.getCard());                            //身份证号
		message.append("。请个人凭身份证至乘车站点自助机或对应窗口取票。");    
//		BusStationMesInfo station = busService.getStationPhone(busDoPayInfo.getOrigin(),busDoPayInfo.getStationName());
		BusStationMesInfo station = new BusStationMesInfo();
		station.setStationPhone("0871-3833680");
		if(station != null && StringUtils.isNotBlank(station.getStationPhone())){
			message.append("客服电话" + station.getStationPhone());
		}
		return message.toString();
	}
	
	/**
	 * 汽车票发车时间时间格式转换
	 * @param originTime
	 * @return
	 */
	public String getMessageTime(String originTime){
		String realTime = originTime;
		
		SimpleDateFormat originSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat realSdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
		try {
			Date date = originSdf.parse(originTime);
			realTime = realSdf.format(date);
			
		} catch (ParseException e) {
			log.error("time changeException : originTime =>" + originTime + ",Exception =>" +e.getMessage(),e);
		}
		return realTime;
	}
	
	
	
	/**
	 * 功能异常时通知指定用户
	 * @param message
	 */
	public void ExceptionSendMessage(String message){
		/*String ExceptionUser = ErkuaiinterfaceConfigUtil.getExceptionUser();
		if(StringUtils.isNotBlank(ExceptionUser)){
			String[] userArr = ExceptionUser.split(",");
			for(String userId : userArr){
				sendMessage(userId,message);
			}
		}*/
		// TODO: 2017/12/21
	}
	
	
	
	
	/**
	 * 获取半个小时前的时间
	 * @return
	 */
	public String getTimeHour(){
		SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calNow = Calendar.getInstance();
		calNow.add(Calendar.MINUTE, -30);
		Date date = calNow.getTime();
		return sdfTime.format(date);
		
	}
	
    class MyThread extends Thread{
		
		private int num;
		 String token = "";
    	 String origin = "";
    	 String site = "";
    	 String date = "";
		public MyThread(int _num,String token,String origin,String site,String date){
			num = _num;
			this.token = token;
			this.origin = origin;
			this.site = site;
			this.date = date;
		}
		public void run(){
			try{
				long start = System.currentTimeMillis();
	    		String result = busClassQuery(token, origin, site,date);
	    		long cost = System.currentTimeMillis() - start;
	    		System.out.println("num=>" + num + " result=>" + result);
	        	System.out.println("num=>" + num + " cost=>" + cost);
	    	}catch(Exception e){
	    		System.out.println("Exception=>" + e + " Msg=>" + e.getMessage());
	    	}
		}
	}


	public BusTicketOrder doReturnBusOrder(String userId,String token,String orderCode) throws Exception{
		if(StringUtils.isBlank(orderCode)){
			log.error("doReturnBusOrder orderCodeNullException :userId =>" + userId);
			return null;
		}
		String resultJson=queryOrder(token,orderCode);
		JSONObject jsonObject = JSONObject.fromObject(resultJson);
		log.info("BusService returnBusOrder :userId =>" + userId + ",orderCode=>" + orderCode + ",resultJson=>" + resultJson);
		String reason = getErrorInfo(jsonObject,"returnBusOrder");
		if(StringUtils.isNotBlank(reason)){
			log.error("doReturnBusOrder orderQueryException :userId =>" + userId + ",orderCode =>" + orderCode + ",reason =>" +reason);
			return null;
		}

		ObjectMapper om=new ObjectMapper();
		BusTicketOrder busTicketOrder=om.readValue(resultJson, BusTicketOrder.class);
		log.info("BusService returnBusOrder :userId =>" + userId + ",BusTicketOrder =>" + busTicketOrder);
		if(!"交易关闭".equals(busTicketOrder.getStatus())){
			String passWord = "";
			if(!"预订".equals(busTicketOrder.getStatus())){
				passWord = getDesVcode(busTicketOrder.getPassword());
			}
			busTicketOrder.setPassword(passWord);
			String cardNo = getDesVcode(busTicketOrder.getCardNo());
			busTicketOrder.setCardNo(cardNo);
//			List<BusStationMesInfo> stations = busServiceDao.selectObject(SqlUtil.BUS_STATION_QUERY, BusStationMesInfo.class, busTicketOrder.getOrigin(),busTicketOrder.getStationName());
//			log.info("BusService returnBusOrder origin="+busTicketOrder.getOrigin() +",stationName="+busTicketOrder.getStationName()+",stations =" +stations);
//			if(stations != null && stations.size()>0){
			BusStationMesInfo station = grpcService.getStation(busTicketOrder.getOrigin(),busTicketOrder.getStationName());
			if(station != null){
				String stationPhone = station.getStationPhone();
				if(StringUtils.isNotBlank(station.getStationAddress())){
					busTicketOrder.setStationAddress(station.getStationAddress());
				}
				if(StringUtils.isNotBlank(stationPhone)){
					List<String> stationPhones = new ArrayList<String>();
					String[] args = stationPhone.split(",");
					for(int i =0; i<args.length; i++){
						String phone = args[i];
						if(StringUtils.isNotBlank(phone)){
							stationPhones.add(phone);
						}
					}
					busTicketOrder.setStationPhones(stationPhones);
				}
			}
			List<InsuranceInfo> insuranceInfo = busTicketOrder.getInsuranceInfo();
			if(insuranceInfo.size()>0){
				double price = insuranceInfo.get(0).getPrice()*0.01;
				busTicketOrder.setInsPrice(price+"");
				busTicketOrder.setInsNum(insuranceInfo.size()+"");
			}else{
				busTicketOrder.setInsPrice("0");
				busTicketOrder.setInsNum("0");
			}
			busTicketOrder.setInsuranceInfo(null);
			return busTicketOrder;

		}
		log.info("BusService BusOrderResponse BusTicketOrder=>" + busTicketOrder);
		return null;
	}

	//判断返回结果是否是正确的
	public String getErrorInfo(JSONObject jsonObject,String methodName){
		String reason = "";
		if(jsonObject.containsKey("errcode") && jsonObject.containsKey("errmsg")){
			reason = jsonObject.getString("errmsg");
			log.info("BusService " + methodName + " ErrorCode:" + jsonObject.getString("errcode") + " Message:"+reason );
		}
		return reason;
	}


	private void caculateArrivalTime(BusClassInfo busClassInfo){
		BigDecimal licheng = new BigDecimal(busClassInfo.getStationLeg());
		BigDecimal minutes = licheng.divide(new BigDecimal(65),0).multiply(new BigDecimal(60));
		StringBuffer dateTime = new StringBuffer();//发车时间
		dateTime.append(busClassInfo.getUseDate());
		dateTime.append(" ");
		dateTime.append(busClassInfo.getUseTime());
		dateTime.append(":00");

		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(dateTime.toString()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.MINUTE,minutes.intValue());
		busClassInfo.setExpectArrivalTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime()));
	}


	private String getParamString(String... args){
		StringBuffer buf = new StringBuffer();
		for(String param : args){
			buf.append("<param>" + param + "</param>");
		}
		return buf.toString();
	}
}
