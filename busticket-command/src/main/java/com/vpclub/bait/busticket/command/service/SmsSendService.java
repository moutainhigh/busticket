package com.vpclub.bait.busticket.command.service;

import com.vpclub.bait.busticket.api.config.SmsConifg;
import com.vpclub.bait.busticket.api.service.ISmsSendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SmsSendService implements ISmsSendService{
    @Autowired
    SmsConifg conifg;
    static final String MSG_RSPCODE="00000";
    public boolean sendMessage(String userId, String serviceId, String password, String paramsString){
        String url=conifg.getUrl();

        StringBuffer sb=new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

        sb.append("<ynicity>");
        sb.append("<serviceId>"+serviceId+"</serviceId>");
        sb.append("<password>"+password+"</password>");
        sb.append("<tel>"+userId+"</tel>");
        sb.append("<correlator>"+123456789+"</correlator>");

        sb.append("<params>");
        sb.append(paramsString);
        sb.append("</params>");

        sb.append("</ynicity>");
        String requestBody=sb.toString();

        String result=getRestful(url, requestBody, "POST","application/xml","application/xml");
        log.info("sendMessage userId =>" + userId + " requestBody=>" + requestBody + " result=>" + result);
        if(StringUtils.isNotBlank(result)){
            String rspCode = getResult(result);
            if(MSG_RSPCODE.equals(rspCode)){
                return true;
            }
        }
        return false;
    }


    private String getRestful(String url, String body, String method,String contentType,String accept) {
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Accept", accept);
        headers.put("Content-Type", contentType);
        return getRestful(url,body,method,headers);
    }

    private String getRestful(String url, String body,String method,Map<String,String> headers) {
        String result = "";
        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod(method);
            if(headers != null && headers.size() > 0){
                Set<Map.Entry<String, String>> entrys = headers.entrySet();
                for(Map.Entry<String, String> entry : entrys){
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.setDoOutput(true);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            if (null != body) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(body.getBytes());
                outputStream.flush();
            }

            if (connection.getResponseCode() != 200) {
                int responseCode = connection.getResponseCode();
                throw new Exception("ResponseCode Error:"+ responseCode);
            }

            BufferedReader responseBuffer = new BufferedReader(
                    new InputStreamReader((connection.getInputStream())));

            StringBuffer response = new StringBuffer();
            String output = "";
            while ((output = responseBuffer.readLine()) != null) {
                response.append(output);
            }
            result = response.toString();
            log.info("GetRestful Result=>Url:" + url + " Method:" + method + " Headers:" + headers + " Body:" + body + " Result:" + result);
        } catch (Exception e) {
            log.error("GetRestful Exception=>Url:" + url + " Method:" + method + " Headers:" + headers + " Body:" + body + " Msg:" + e.getMessage(), e);
        }finally{
            if(connection != null){
                connection.disconnect();
            }
        }
        return result;

    }

    private String getResult(String result){
        String rspCode="<rspCode>(.*)</rspCode>";
        rspCode = getRegValue(result,rspCode);
        return rspCode;
    }

    /*
     * 根据正则表达式获取XML中对应属性的值
     */
    private String getRegValue(String content, String reg) {
        Pattern p1 = Pattern.compile(reg);
        Matcher m1 = p1.matcher(content);
        if (m1.find()) {
            System.out.println(m1.group(1));
            return m1.group(1);
        }
        return null;
    }

}
