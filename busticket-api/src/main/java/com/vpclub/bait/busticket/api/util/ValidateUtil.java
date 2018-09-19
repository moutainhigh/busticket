package com.vpclub.bait.busticket.api.util;

import cn.vpclub.moses.utils.common.JsonUtil;
import cn.vpclub.moses.utils.common.StringUtil;
import cn.vpclub.moses.utils.constant.SystemConstant;
import cn.vpclub.moses.utils.web.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.comparators.ComparableComparator;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
public class ValidateUtil {

        //允许访问的app :10000168
        static  Map<String,String>  appMap= new HashMap<String,String>(){
            {
                put("10000168","1a3b2d4k6f");
            }
        };


    /**
     * 验证app版本号
      * @param appId
     * @return
     */
    public static Boolean isVerificationApp(String appId){
        return StringUtil.isEmpty(appMap.get(appId));
    }

    /**
     * 验证签名
     * @param map
     * @return
     */
    public static Boolean isVerificationSign(Map<String, ? extends Object> map){
        String reString = getMD5SignFromMap(map,appMap.get(map.get("appID")));
        return map.get("sign").equals(reString);
    }

    /**
     * MD5签名算法
     *
     * @param map 要参与签名的数据Map
     * @param apiKey API密钥
     * @return 签名
     */
    public static String getMD5SignFromMap(Map<String, ? extends Object> map, String apiKey) {
        return getSignFromMap(map, apiKey, SystemConstant.MD5);
    }

    /**
     * 签名算法
     *
     * @param map 要参与签名的数据Map
     * @param apiKey API密钥
     * @param algorithm 运算法测
     * @return 签名
     */
    public static String getSignFromMap(Map<String, ? extends Object> map, String apiKey,
                                        String algorithm) {
        ArrayList<String> list = new ArrayList<String>();
        if (null != map && !map.isEmpty()) {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                if (StringUtil.isEmpty(entry.getValue())
                        || "sign".equalsIgnoreCase(entry.getKey())) {
                    continue;
                }

                if (entry.getValue() instanceof Collection
                        && !((Collection) entry.getValue()).isEmpty()) {
                    list.add(entry.getKey() + "=" + JsonUtil.objectToJson(entry.getValue()) + "&");
                } else if (entry.getValue() instanceof Map
                        && !((Map) entry.getValue()).isEmpty()) {
                    list.add(entry.getKey() + "=" + JsonUtil.objectToJson(entry.getValue()) + "&");
                } else {
                    list.add(entry.getKey() + "=" + entry.getValue() + "&");
                }
            }
        }
        return createSign(list, apiKey, algorithm);
    }

    /**
     * 生成签名
     *
     * @param list (key=value&)类型字符串列表
     * @param apiKey 签名密钥
     * @param algorithm 运算法测
     * @return 签名
     */
    private static String createSign(ArrayList<String> list, String apiKey, String algorithm) {
        String result = null;
        if (null != list && !list.isEmpty()) {
            int size = list.size();
            String[] arrayToSort = list.toArray(new String[size]);
            Arrays.sort(arrayToSort, new ComparableComparator());
            StringBuilder stringSignTemp = new StringBuilder();
            for (int i = 0; i < size; i++) {
                stringSignTemp.append(arrayToSort[i]);
            }
            String signTempStr;
            if (!StringUtil.isEmpty(apiKey)) {
                stringSignTemp.append("key=" + apiKey);
                signTempStr = stringSignTemp.toString();
            } else {
                signTempStr = stringSignTemp.substring(0, stringSignTemp.length() - 1);
            }
            log.info("createSign Before MD5:" + signTempStr);
            result = encodeToAlgorithm(signTempStr, algorithm).toUpperCase();
        }
        log.info("createSign Result:" + result);
        return result;
    }

    public static String encodeToAlgorithm(String str, String algorithm) {
        if (StringUtil.isEmpty(str)) {
            return null;
        } else {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
                byte[] digest = messageDigest.digest(str.getBytes("utf-8"));
                return new String(Hex.encodeHex(digest));
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                throw Exceptions.unchecked(e);
            }
        }
    }
}
